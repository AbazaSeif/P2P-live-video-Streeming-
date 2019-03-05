package com.p2p.videos.service;

import com.p2p.files.service.FileRestService;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.peers.service.PeerRestService;
import com.p2p.videos.model.VideoStream;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

@Component
public class VideoStreamRestService extends PeerRestService {

    private static final Logger LOG = Logger.getLogger(FileRestService.class);

    private VideoStreamApi videoStreamApi;

    @Override
    protected void initializeService() {
        this.videoStreamApi = getRestClient().create(VideoStreamApi.class);
    }

    public VideoStream getVideoStream(String videoStreamId, Peer peer) {
        return makeRequestToPeer(peer, videoStreamApi.getVideoStream(videoStreamId));
    }

    public List<VideoStream> getVideoStreamsFromPeer(String fileHash, BooleanStatus streamStatus, Peer peer) {
        return makeRequestToPeer(peer, videoStreamApi.getVideoStreams(fileHash, streamStatus));
    }

    public Map<Peer, List<VideoStream>> getVideoStreamsFromNetwork(String fileHash, BooleanStatus fileStreamStatus,
                                                                   BooleanStatus onlineStatus,
                                                                   BooleanStatus streamStatus) {
        return makeRequestToAllPeers(videoStreamApi.getVideoStreams(fileHash, fileStreamStatus), onlineStatus,
                streamStatus);
    }

    public interface VideoStreamApi {

        @GET("videos/streams/{videoStreamId}")
        Call<VideoStream> getVideoStream(@Path("videoStreamId") String videoStreamId);

        @POST("videos/streams")
        Call<String> createVideoStream(@Body Map<String, Object> requestParameters);

        @GET("videos/streams")
        Call<List<VideoStream>> getVideoStreams(@Query("file_hash") String fileHash,
                                                @Query("status") BooleanStatus statusString);
    }
}
