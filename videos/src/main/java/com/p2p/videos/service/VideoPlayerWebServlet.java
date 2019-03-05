package com.p2p.videos.service;

import com.p2p.exceptions.CoreException;
import com.p2p.files.models.UploadedFile;
import com.p2p.files.models.FileChunk;
import com.p2p.files.service.UploadedFileService;
import com.p2p.files.utils.FileIOUtils;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.peers.service.AbstractWebServlet;
import com.p2p.peers.service.PeerService;
import com.p2p.utils.ParameterParser;
import com.p2p.videos.model.VideoStream;
import com.p2p.videos.player.P2PStreamingMediaPlayerEventAdapter;
import com.p2p.videos.player.P2PViewingMediaPlayerEventAdapter;
import com.p2p.videos.player.VideoPlayer;
import com.p2p.videos.service.config.VideoConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/videos")
public class VideoPlayerWebServlet extends AbstractWebServlet {

    private static final Logger LOG = Logger.getLogger(VideoPlayerWebServlet.class);

    @Autowired
    private ParameterParser parameterParser;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private FileIOUtils fileIOUtils;
    @Autowired
    private UploadedFileService uploadedFileService;
    @Autowired
    private VideoStreamService videoStreamService;
    @Autowired
    private PeerService peerService;
    @Autowired
    private VideoConfiguration videoConfiguration;

    @RequestMapping(path = "streams", method = RequestMethod.POST, produces = "application/json")
    public String createVideoStream(@RequestBody Map<String, Object> requestParameters) {
        String name = parameterParser.getStringParameter(requestParameters, "name", true);
        String fileHash = parameterParser.getStringParameter(requestParameters, "hash", true);

        UploadedFile uploadedFile = uploadedFileService.getUploadedFileByHash(fileHash);
        if (uploadedFile == null) {
            throw new CoreException.NotFoundException("file with hash %s doesnt exist", fileHash);
        }
        List<FileChunk> fileChunks = uploadedFileService.getUploadedFileChunksByFile(uploadedFile);
        VideoStream videoStream = new VideoStream();
        videoStream.setChunks(CollectionUtils.size(fileChunks));
        videoStream.setDuration(0l);
        videoStream.setStatus(BooleanStatus.CREATED);
        videoStream.setVideoStreamName(name);
        videoStream.setUploadedFile(uploadedFile);

        return videoStreamService.createVideoStream(videoStream);
    }

    @RequestMapping(path = "streams/{streamId}", method = RequestMethod.GET, produces = "application/json")
    public VideoStream getVideoStream(@PathVariable("streamId") String videoStreamId) {
        VideoStream stream = videoStreamService.getVideoStream(videoStreamId, true);
        if (stream == null) {
            throw new CoreException.NotFoundException("video stream with id %s doesnt exist", videoStreamId);
        }
        return stream;
    }

    @RequestMapping(path = "streams", method = RequestMethod.GET, produces = "application/json")
    public List<VideoStream> getVideoStreams(@RequestParam(name = "file_hash", required = false) String fileHash,
                                             @RequestParam(name = "status", required = false)
                                                     String streamStatusString) {
        UploadedFile uploadedFile = null;
        if (StringUtils.isNotEmpty(fileHash)) {
            uploadedFile = uploadedFileService.getUploadedFileByHash(fileHash);
        }
        BooleanStatus streamStatus =
                parameterParser.getEnumTypeFromString(streamStatusString, "status", BooleanStatus.class, true);

        return videoStreamService.getVideoStreams(uploadedFile, streamStatus, "uploadedFile");
    }

    @RequestMapping(path = "streams/network", method = RequestMethod.GET, produces = "application/json")
    public Map<String, List<VideoStream>> getVideoStreamsFromNetwork(
            @RequestParam(name = "file_hash", required = false) String fileHash,
            @RequestParam(name = "status", required = false) String streamStatusString,
            @RequestParam(name = "peer_status", required = false) String peerStatusString,
            @RequestParam(name = "streaming", required = false) String streamingStatusString) {
        UploadedFile uploadedFile = null;
        if (StringUtils.isNotEmpty(fileHash)) {
            uploadedFile = uploadedFileService.getUploadedFileByHash(fileHash);
        }
        BooleanStatus streamStatus =
                parameterParser.getEnumTypeFromString(streamStatusString, "status", BooleanStatus.class, false);
        BooleanStatus peerStatus =
                parameterParser.getEnumTypeFromString(peerStatusString, "peer_status", BooleanStatus.class, false);
        BooleanStatus streamingStatus =
                parameterParser.getEnumTypeFromString(streamingStatusString, "streaming", BooleanStatus.class, false);

        Map<Peer, List<VideoStream>> videoStreams =
                videoStreamService.getStreamsFromNetwork(fileHash, streamStatus, peerStatus, streamingStatus);

        return convertPeerMapToPeerUrlMap(videoStreams);
    }

    @RequestMapping(path = "streams/{streamId}/start", method = RequestMethod.POST, produces = "application/json")
    public void startVideoStream(@PathVariable("streamId") String streamId) {
        VideoStream videoStream = videoStreamService.getVideoStream(streamId, true);
        if (videoStream == null) {
            throw new CoreException.NotFoundException("stream with id %s doesnt exist");
        }
        if (CollectionUtils
                .isNotEmpty(videoStreamService.getVideoStreams(videoStream.getUploadedFile(), BooleanStatus.ACTIVE))) {
            throw new CoreException.NotValidException("there can be only one stream for a uploaded file %s",
                    videoStream.getUploadedFile().getFileName());
        }
        videoStreamService.startMediaPlayer(videoStream,
                new P2PStreamingMediaPlayerEventAdapter(videoStream, videoStreamService));
    }

    @RequestMapping(path = "streams/start", method = RequestMethod.POST, produces = "application/json")
    public String createAndStartVideoStream(@RequestBody Map<String, Object> requestParameters) {
        String streamId = createVideoStream(requestParameters);
        startVideoStream(streamId);
        return streamId;
    }

    @RequestMapping(path = "streams/join", method = RequestMethod.POST, produces = "application/json")
    public void joinStream(@RequestBody Map<String, Object> requestParameters) {
        String fileHash = parameterParser.getStringParameter(requestParameters, "file_hash", false);
        BooleanStatus streamStatus =
                parameterParser.getEnumTypeFromString(requestParameters, "status", BooleanStatus.class, false);
        String ip = parameterParser.getStringParameter(requestParameters, "ip", true);
        String port = parameterParser.getStringParameter(requestParameters, "port", true);
        String outputFileName = parameterParser.getStringParameter(requestParameters, "output", true) +
                RandomStringUtils.randomAlphabetic(3) + ".part";

        Peer peer = peerService.getPeerByIpAndPort(ip, port);
        if (peer == null) {
            throw new CoreException.NotFoundException("peer with ip %s and port %s doesnt exist", ip, port);
        }

        List<VideoStream> videoStreams = videoStreamService.getVideoStreamsFromPeer(fileHash, streamStatus, peer);
        if (CollectionUtils.isNotEmpty(videoStreams)) {
            videoStreams.forEach(stream -> {
                UploadedFile file = stream.getUploadedFile();
                List<FileChunk> fileChunks =
                        uploadedFileService.getUploadedFileChunksByFileHash(file.getFileHash(), peer);

                List<FileChunk> neededFileChunks = new ArrayList<FileChunk>();
                neededFileChunks.add(fileChunks.get(0));
                int startChunkNumber = videoStreamService.getStartChunkNumber(stream);
                int size = CollectionUtils.size(fileChunks);
                if (startChunkNumber >= 0 && startChunkNumber <= size) {
                    CollectionUtils.addAll(neededFileChunks, fileChunks.subList(startChunkNumber, size));
                    Collections.sort(neededFileChunks, (o1, o2) -> (int) (o1.getFileOffset() - o2.getFileOffset()));
                    File outputFile = FileUtils.getFile(outputFileName);
                    try {
                            int endIndex = Math.min(size, videoConfiguration.preDownloadChunks);
                        downloadVideoChunksToFile(neededFileChunks.subList(0, endIndex), outputFile);
                        LOG.error("Video downloaded");
                        VideoPlayer videoPlayer=new VideoPlayer(videoConfiguration);
                        videoPlayer.startPlayer(outputFile, new P2PViewingMediaPlayerEventAdapter(stream));
                        taskExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                List<FileChunk> sublist =
                                        neededFileChunks.subList(endIndex, CollectionUtils.size(neededFileChunks));
                                LOG.info("Need to download " + CollectionUtils.size(sublist));
                                downloadVideoChunksToFile(sublist, outputFile);
                            }
                        });
                        FileUtils.forceDeleteOnExit(outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            throw new CoreException.NotValidException("Peer %s is not playing any stream" + peer.toString());
        }
    }

    private void downloadVideoChunksToFile(List<FileChunk> fileChunks, File outputFile) {
        if (CollectionUtils.isNotEmpty(fileChunks)) {
            fileChunks.forEach(chunk -> {
                try {
                    LOG.info("Downloading chunk " + chunk.getChunkHash());
                    FileOutputStream outputStream =
                            FileUtils.openOutputStream(outputFile, true);
                    InputStream inputStream = downloadFileChunk(chunk);
                    IOUtils.copy(inputStream, outputStream);
                    uploadedFileService.closeSilently(outputStream, inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private InputStream downloadFileChunk(FileChunk fileChunk) throws IOException {
        Map<Peer, FileChunk> fileChunksPeerMap = uploadedFileService
                .getUploadedFileChunkFromNetwork(fileChunk.getChunkHash(), BooleanStatus.ACTIVE, null);
        Set<Map.Entry<Peer, FileChunk>> fileChunkSet = fileChunksPeerMap.entrySet();
        if (CollectionUtils.isNotEmpty(fileChunkSet)) {
            Map.Entry<Peer, FileChunk> randomPeer =
                    IterableUtils.get(fileChunkSet, (new Random().nextInt(10000)) % CollectionUtils.size(fileChunkSet));
            return uploadedFileService.downloadUploadedFileChunk(randomPeer.getValue(), randomPeer.getKey());
        } else {
            throw new CoreException.NotFoundException("chunks unavailable. Closing");
        }
    }

    @RequestMapping(path = "playstream", method = RequestMethod.POST, produces = "application/json")
    public void playVideChunked(@RequestBody Map<String, Object> requestParameters) {
        String fileHash = parameterParser.getStringParameter(requestParameters, "file_hash", true);
        String outputFileName = parameterParser.getStringParameter(requestParameters, "output", true) +
                RandomStringUtils.random(3) + ".part";
        List<String> fileChunks = parameterParser.getListStringParameters(requestParameters, "chunks", true);

        Map<Peer, List<UploadedFile>> fileMap =
                uploadedFileService.getFilesByHashFromNetwork(Arrays.asList(fileHash), BooleanStatus.ACTIVE, null);
        Set<Map.Entry<Peer, List<UploadedFile>>> fileMapEntries = fileMap.entrySet();
        if (CollectionUtils.isNotEmpty(fileMapEntries)) {
            fileMapEntries.forEach(entry -> {
                List<UploadedFile> uploadedFiles = entry.getValue();
                if (CollectionUtils.isNotEmpty(uploadedFiles)) {
                    UploadedFile file = uploadedFiles.get(0);
                    List<FileChunk> uploadedFileChunks =
                            uploadedFileService.getUploadedFileChunksByFileHash(file.getFileHash(), entry.getKey());
                    fileChunks.add(uploadedFileChunks.get(0).getChunkHash());
                    List<FileChunk> neededFileChunks = uploadedFileChunks.stream()
                            .filter(uploadedFileChunk -> fileChunks.stream().anyMatch(
                                    chunkHash -> StringUtils.equals(uploadedFileChunk.getChunkHash(), chunkHash)))
                            .collect(Collectors.toList());
                    Collections.sort(neededFileChunks, new Comparator<FileChunk>() {
                        @Override
                        public int compare(FileChunk o1, FileChunk o2) {
                            return (int) (o1.getFileOffset() - o2.getFileOffset());
                        }
                    });
                    File outputFile = FileUtils.getFile(outputFileName);
                    try {
                        FileOutputStream outputStream = FileUtils.openOutputStream(outputFile, true);
                        File firstChunk = uploadedFileService.getFileChunk(neededFileChunks.get(0));
                        FileUtils.copyFile(firstChunk, outputFile);
                        uploadedFileService.closeSilently(outputStream);
//                        videoPlayer.startPlayer(outputFileName);
                        taskExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                List<FileChunk> sublist =
                                        neededFileChunks.subList(1, CollectionUtils.size(neededFileChunks));
                                sublist.forEach(chunk -> {
                                    try {
                                        FileOutputStream outputStream = FileUtils.openOutputStream(outputFile, true);
                                        File uploadedChunkFile = uploadedFileService.getFileChunk(chunk);
                                        FileUtils.copyFile(uploadedChunkFile, outputStream);
                                        uploadedFileService.closeSilently(outputStream);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
