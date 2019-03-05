package com.p2p.files.service;

import com.p2p.files.models.UploadedFile;
import com.p2p.files.models.FileChunk;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.peers.service.PeerRestService;
import okhttp3.ResponseBody;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Peer rest service.
 * This class is used to contact other peers and retrieve appropriate information
 */
@Component
public class FileRestService extends PeerRestService {

    private static final Logger LOG = Logger.getLogger(FileRestService.class);
    private FileApi peerApi;
    @Override
    protected void initializeService() {
        this.peerApi = getRestClient().create(FileApi.class);
    }

    public Map<Peer, List<UploadedFile>> getFilesByHashFromNetwork(List<String> filesHashes, BooleanStatus peerStatus,
                                                                   BooleanStatus streamingStatus) {
        return makeRequestToAllPeers(peerApi.getFilesByHashFromNetwork(filesHashes), peerStatus, streamingStatus);
    }

    public List<FileChunk> getUploadedFileChunksByFileHash(String fileHash, Peer peer) {
        return makeRequestToPeer(peer, peerApi.getUploadedFileChunksByFileHash(fileHash));
    }

    public FileChunk getUploadedFileChunkByChunkHash(String chunkHash, Peer peer) {
        return makeRequestToPeer(peer, peerApi.getUploadedFileChunkByChunkHash(chunkHash));
    }

    public Map<Peer, FileChunk> getUploadedFileChunkFromNetwork(String chunkHash, BooleanStatus peerStatus,
                                                                BooleanStatus streamingStatus) {
        return makeRequestToAllPeers(peerApi.getUploadedFileChunkByChunkHash(chunkHash), peerStatus, streamingStatus);
    }

    public InputStream downloadFileFromPeer(String chunkHash, Peer peer) {
        Map<String, Object> requestParamaters = new HashMap<>();
        requestParamaters.put("chunk_hash", chunkHash);
        return makeRequestToPeer(peer, peerApi.downloadFileChunk(requestParamaters)).byteStream();
    }

    public interface FileApi {

        @GET("files/hashes")
        Call<List<UploadedFile>> getFilesByHashFromNetwork(@Query("hash") List<String> fileHashes);

        @GET("files")
        Call<List<UploadedFile>> getAllFiles(@Query("status") String status);

        @GET("files/chunks")
        Call<List<FileChunk>> getUploadedFileChunksByFileHash(@Query("hash") String fileHash);

        @GET("files/chunks/{chunkHash}")
        Call<FileChunk> getUploadedFileChunkByChunkHash(@Path("chunkHash") String chunkHash);

        @POST("files/chunks/download")
        @Streaming
        Call<ResponseBody> downloadFileChunk(@Body Map<String, Object> requestParameters);
    }
}
