package com.p2p.files.service;

import com.p2p.exceptions.CoreException;
import com.p2p.files.models.FileRange;
import com.p2p.files.models.UploadedFile;
import com.p2p.files.models.FileChunk;
import com.p2p.files.utils.FileIOUtils;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.peers.service.PeerService;
import com.p2p.utils.ParameterParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
  This class use to trigger file related functionality on the peer.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/files")
public class UploadedFileServlet {

    private static final Logger LOG = Logger.getLogger(UploadedFileServlet.class);
    @Autowired
    private FileIOUtils fileIOUtils;
    @Autowired
    private UploadedFileService uploadedFileService;
    @Autowired
    private ParameterParser parameterParser;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private PeerService peerService;

    @RequestMapping(method = RequestMethod.GET, path = "ranges", produces = "application/json")
    public List<FileRange> getFileRanges(@RequestParam("file_hash") String fileHash) {
        // This will query the database with given file hash and will throw exception if the file doesnt exist with hash
        UploadedFile uploadedFile = uploadedFileService.getUploadedFileByHash(fileHash);
        if (uploadedFile == null) {
            throw new CoreException.NotFoundException("file with hash %s doesnt exist", fileHash);
        }
        // Will create a #File object from file path. This #File handle is used to access the file.
        File file = uploadedFileService.getFile(uploadedFile.getFilePath(), true);
        // Will calculate the ranges for #File
        return fileIOUtils.getFileRanges(file);
    }

    /**
     * Index directory list. This will read all the files in the directory and add them to Database as #UploadedFile. We
     * are calling this process as indexing.
     */
    @RequestMapping(method = RequestMethod.POST, path = "index", produces = "application/json")
    public List<UploadedFile> indexDirectory(@RequestBody Map<String, Object> requestParameters) throws IOException {
        // Directory to be read
        String directoryPath = parameterParser.getStringParameter(requestParameters, "directory", true);
        // Returns file handle for given directory path
        File directory = fileIOUtils.getDirectory(directoryPath);
        // Extetnsions of file that need to be indexed
        List<String> extensions = parameterParser.getListStringParameters(requestParameters, "extensions", false);

        try {
            // Reads all the file in the given directory and returns the entries that is created in database
            return uploadedFileService
                    .indexDirectory(directory, uploadedFileService.getExtensionFilter(extensions),
                            TrueFileFilter.INSTANCE);
        } catch (Exception e) {
            //This happens only when there is a duplicate file. It will be gone next time. May be a better handling should be inplace
            return uploadedFileService.indexDirectory(directory, uploadedFileService.getExtensionFilter(extensions),
                    TrueFileFilter.INSTANCE);
        }
    }

    /**
     * Gets files by hashes.
     */
    @RequestMapping(method = RequestMethod.GET, path = "hashes", produces = "application/json")
    public List<UploadedFile> getFilesByHashes(@RequestParam("hash") List<String> fileHashes) {
        // Queries databse and retrieve all the entries identified by hash
        return uploadedFileService.getUploadedFilesByHash(fileHashes);
    }

    /**
     * Gets uploaded file chunks. Retrieves all the #FileChunk for the file hash. To trigger functionality
     * http://server_addr:port_num/files/chunks
     */
    @RequestMapping(method = RequestMethod.GET, path = "chunks", produces = "application/json")
    public List<FileChunk> getUploadedFileChunks(@RequestParam("hash") String fileHash) {
        UploadedFile uploadedFile = uploadedFileService.getUploadedFileByHash(fileHash);
        if (uploadedFile == null) {
            throw new CoreException.NotFoundException("file with hash %s doesnt exist ", fileHash);
        }
        return uploadedFileService.getUploadedFileChunksByFile(uploadedFile);
    }

    /**
     * Gets uploaded file chunk. Retrieves the #FileChunk by chunkHash. To trigger functionality
     * http://:address/files/chunks/{hash of chunk}
     */
    @RequestMapping(method = RequestMethod.GET, path = "chunks/{chunkHash}", produces = "application/json")
    public FileChunk getUploadedFileChunk(@PathVariable("chunkHash") String chunkHash) {
        FileChunk fileChunk = uploadedFileService.getUploadedFileChunkByHash(chunkHash);
        if (fileChunk == null) {
            throw new CoreException.NotFoundException("file chunk with hash %s doesnt exist ", chunkHash);
        }
        return fileChunk;
    }

    @RequestMapping(method = RequestMethod.GET, path = "chunks/{chunkHash}/network", produces = "application/json")
    public Map<String, FileChunk> getUploadedFileChunkFromNetwork(@PathVariable("chunkHash") String chunkHash,
                                                                  @RequestParam(name = "status",
                                                                          required = false)
                                                                          String peerStatusString,
                                                                  @RequestParam(name = "streaming",
                                                                          required = false)
                                                                          String streamingStatusString) {

        BooleanStatus peerStatus =
                parameterParser.getEnumTypeFromString(peerStatusString, "status", BooleanStatus.class, false);
        BooleanStatus streamingStatus =
                parameterParser.getEnumTypeFromString(streamingStatusString, "streaming", BooleanStatus.class, false);
        Map<Peer, FileChunk> peerFileMap =
                uploadedFileService.getUploadedFileChunkFromNetwork(chunkHash, peerStatus, streamingStatus);
        return peerFileMap.entrySet().stream()
                .collect(Collectors.toMap(e -> Peer.getPeerUrl(e.getKey()), Map.Entry::getValue));
    }

    /**
     * Download chunks file system resource. Download a file chunk from identifies by chunk_hash.
     */
    @RequestMapping(method = RequestMethod.POST, path = "chunks/download",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource downloadChunks(@RequestBody Map<String, Object> requestParameters,
                                             HttpServletResponse servletResponse) throws IOException {
        String chunkHash = parameterParser.getStringParameter(requestParameters, "chunk_hash", true);
        FileChunk fileChunk = uploadedFileService.getUploadedFileChunkByHash(chunkHash);
        if (fileChunk == null) {
            throw new CoreException.NotFoundException("chunk with hash %s doesnt exist", chunkHash);
        }
        File chunkFile = uploadedFileService.getFileChunk(fileChunk);
        servletResponse.setHeader("Content-Disposition", "attachment; filename=\"somefile.pdf\"");
        return new FileSystemResource(chunkFile);
    }

    /**
     *  This will download the file chunk from given peer. It will trigger the functionality
     * downloadChunks(Map, HttpServletResponse)} on given peer.
     */
    @RequestMapping(method = RequestMethod.POST, path = "chunks/download/individiual")
    public void downloadChunkFromPeer(@RequestBody Map<String, Object> requestParameters) throws IOException {
        String chunkHash = parameterParser.getStringParameter(requestParameters, "chunk_hash", true);
        String peerId = parameterParser.getStringParameter(requestParameters, "peer", true);
        String peerPort = parameterParser.getStringParameter(requestParameters, "port", true);
        Peer peer = peerService.getPeerByIpAndPort(peerId, peerPort);
        FileChunk fileChunk = uploadedFileService.getUploadedFileChunkByChunkHash(chunkHash, peer);

        InputStream stream = uploadedFileService.downloadUploadedFileChunk(fileChunk, peer);
        String fileChunkMd5 = fileIOUtils.getFileMd5(stream);
        LOG.error(String.format("Expected md 5 %s, Actual md5 %s", chunkHash,
                fileChunkMd5));
    }
    /**
     * This will download the entire file from network. It will randomly query all the
     * peers which has the chunk and download.
     */
    @RequestMapping(method = RequestMethod.POST, path = "download")
    public List<FileChunk> downloadFileFromNetwork(@RequestBody Map<String, Object> requestParameters)
            throws IOException {
        String fileHash = parameterParser.getStringParameter(requestParameters, "hash", true);
        BooleanStatus peerStatus =
                parameterParser.getEnumTypeFromString(requestParameters, "status", BooleanStatus.class, false);
        BooleanStatus streamingStatus =
                parameterParser.getEnumTypeFromString(requestParameters, "streaming", BooleanStatus.class, false);
        List<FileChunk> fileChunks =
                uploadedFileService.getUploadedFileChunksFromFileFromNetwork(fileHash, peerStatus, streamingStatus);
        if (CollectionUtils.isNotEmpty(fileChunks)) {
            UploadedFile uploadedFile = fileChunks.get(0).getUploadedFile();

            String downloadFileId = uploadedFileService.createDownloadedFile(uploadedFile);
            UploadedFile downloadedFileDB = uploadedFileService.getUploadedFile(downloadFileId);
            File downloadedFile = uploadedFileService.createFile(downloadedFileDB);

            OutputStream targetFileOutputStream = FileUtils.openOutputStream(downloadedFile);
            fileChunks.forEach(fileChunk -> {
                // get chunk info from network
                Map<Peer, FileChunk> peerFileChunksMap = uploadedFileService
                        .getUploadedFileChunkFromNetwork(fileChunk.getChunkHash(), peerStatus, streamingStatus);
                Set<Map.Entry<Peer, FileChunk>> peerFileChunkMapEntrySet = peerFileChunksMap.entrySet();
                if (CollectionUtils.isNotEmpty(peerFileChunkMapEntrySet)) {
                    int size = IterableUtils.size(peerFileChunkMapEntrySet);
                    //Randomly selected the peer to download the chcunk
                    Map.Entry<Peer, FileChunk> peerEntry =
                            IterableUtils.get(peerFileChunkMapEntrySet, (new Random().nextInt(10000)) % size);

                    try {
                        InputStream stream =
                                uploadedFileService
                                        .downloadUploadedFileChunk(peerEntry.getValue(), peerEntry.getKey());
                        //Donwload and save the chunk
                        IOUtils.copyLarge(stream, targetFileOutputStream);
                        uploadedFileService.closeSilently(stream);
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            });
            uploadedFileService.closeSilently(targetFileOutputStream);
            // see if the we downloaded the correct file.
            String downloadedFileHash = fileIOUtils.getFileMd5(downloadedFile);
            if (!StringUtils.equals(downloadedFileHash, fileHash)) {
                FileUtils.deleteQuietly(downloadedFile);
                throw new CoreException.NotValidException(
                        "downloaded file hashes doesnt match. wrong file downloaded. Delteing");
            }
        }
        return fileChunks;
    }

    /**
     * Will retrieve all the indexed files
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<UploadedFile> getAllFiles(@RequestParam(name = "status", required = false) String statusString) {
        BooleanStatus status =
                parameterParser.getEnumTypeFromString(statusString, "status", BooleanStatus.class, false);
        return uploadedFileService.getAllUploadedFiles(status);
    }

    /**
     * Will retrieve all the Files identified by hash from network
     */
    @RequestMapping(method = RequestMethod.GET, path = "network", produces = "application/json")
    public Map<String, List<UploadedFile>> getFileByHashesFromNetwork(@RequestParam("hash") List<String> fileHashes,
                                                                      @RequestParam(name = "status", required = false)
                                                                              String peerStatusString,
                                                                      @RequestParam(name = "streaming",
                                                                              required = false)
                                                                              String streamingStatusString) {
        BooleanStatus peerStatus =
                parameterParser.getEnumTypeFromString(peerStatusString, "status", BooleanStatus.class, false);
        BooleanStatus streamingStatus =
                parameterParser.getEnumTypeFromString(streamingStatusString, "streaming", BooleanStatus.class, false);
        Map<Peer, List<UploadedFile>> peerFileMap =
                uploadedFileService.getFilesByHashFromNetwork(fileHashes, peerStatus, streamingStatus);
        return peerFileMap.entrySet().stream()
                .collect(Collectors.toMap(e -> Peer.getPeerUrl(e.getKey()), Map.Entry::getValue));
    }

}