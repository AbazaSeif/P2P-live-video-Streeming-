package com.p2p.videos.service;

import com.p2p.files.models.UploadedFile;
import com.p2p.files.service.UploadedFileService;
import com.p2p.files.service.config.FilesConfiguration;
import com.p2p.files.utils.FileIOUtils;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.utils.DateTimeUtils;
import com.p2p.validations.NotEmptyString;
import com.p2p.videos.dao.VideoStreamDao;
import com.p2p.videos.model.VideoStream;
import com.p2p.videos.player.VideoPlayer;
import com.p2p.videos.service.config.VideoConfiguration;
import net.sf.oval.constraint.NotNull;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import java.io.File;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service("videoStreamService")
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Throwable.class)
public class VideoStreamService {

    private static final Logger LOG = Logger.getLogger(VideoStreamService.class);

    @Autowired
    private VideoStreamDao videoStreamDao;
    @Autowired
    private UploadedFileService uploadedFileService;
    @Autowired
    private VideoStreamRestService videoStreamRestService;
    @Autowired
    private DateTimeUtils dateTimeUtils;
    @Autowired
    private FileIOUtils fileIOUtils;
    @Autowired
    private VideoConfiguration videoConfiguration;
    @Autowired
    private FilesConfiguration filesConfiguration;

    public String createVideoStream(@NotNull(message = "video stream cannot be null") VideoStream videoStream) {
        return (String) videoStreamDao.create(videoStream);
    }

    public VideoStream getVideoStream(@NotEmptyString(message = "video stream id cannot be null") String videoStreamId,
                                      boolean eagerLoad) {
        VideoStream videoStream = videoStreamDao.get(VideoStream.class, videoStreamId);
        if (eagerLoad && videoStream != null) {
            videoStream = videoStreamDao.lazyLoadFields(videoStream);
        }
        return videoStream;
    }

    public List<VideoStream> getVideoStreams(UploadedFile uploadedFile, BooleanStatus streamStatus,
                                             String... fetchClasses) {
        return videoStreamDao.getVideoStreams(uploadedFile, streamStatus, fetchClasses);
    }

    public VideoStream getVideoStreamFromPeer(@NotEmptyString(message = "stream id canot be empty") String streamId,
                                              Peer peer) {
        return videoStreamRestService.getVideoStream(streamId, peer);
    }

    public List<VideoStream> getVideoStreamsFromPeer(String fileHash, BooleanStatus streamStatus, Peer peer) {
        return videoStreamRestService.getVideoStreamsFromPeer(fileHash, streamStatus, peer);
    }

    public void updateVideoStream(@NotNull(message = "video stream cannot be null") VideoStream videoStream) {
        videoStreamDao.update(videoStream);
    }

    public void startMediaPlayer(VideoStream videoStream, MediaPlayerEventAdapter mediaPlayerEventAdapter) {
        File file = uploadedFileService.getFile(videoStream.getUploadedFile().getFilePath(), true);
        VideoPlayer videoPlayer=new VideoPlayer(videoConfiguration);
        videoPlayer.startPlayer(file, mediaPlayerEventAdapter);
    }

    public Map<Peer, List<VideoStream>> getStreamsFromNetwork(String fileHash, BooleanStatus fileStreamStatus,
                                                              BooleanStatus peerStatus,
                                                              BooleanStatus streamStatus) {
        return videoStreamRestService.getVideoStreamsFromNetwork(fileHash, fileStreamStatus, peerStatus, streamStatus);
    }

    public int getStartChunkNumber(VideoStream stream) {
        long duration = stream.getDuration();
        long finishedDuration = dateTimeUtils
                .between(stream.getCreatedTime(), dateTimeUtils.getApplicationCurrentTime(), ChronoUnit.MILLIS);
        LOG.error(stream.getCreatedTime());
        LOG.error(dateTimeUtils.getApplicationCurrentTime());
        double percentageFinished = (double) finishedDuration / (double) duration;
        return (int) (((videoConfiguration.playBackRate * percentageFinished) * stream.getChunks()));

    }
}
