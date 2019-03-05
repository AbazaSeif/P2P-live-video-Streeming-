package com.p2p.peers.service;

import com.p2p.model.BooleanStatus;
import com.p2p.network.HostSelectionInterceptor;
import com.p2p.network.NetworkService;
import com.p2p.network.RestService;
import com.p2p.peers.model.Peer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PeerRestService extends RestService {

    private static final Logger LOG = Logger.getLogger(PeerRestService.class);
    private PeerApi peerApi;
    @Autowired
    private PeerNetworkService peerNetworkService;
    @Autowired
    private HostSelectionInterceptor hostSelectionInterceptor;
    @Autowired
    private PeerService peerService;
    @Override
    protected void initializeService() {
        this.peerApi = getRestClient().create(PeerApi.class);
    }
    @Override
    protected NetworkService getNetworkService() {
        return peerNetworkService;
    }

    public void sendNewPeersToPeer(Peer peer, List<Peer> peers) {
        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put("peers", peers);
        hostSelectionInterceptor.setHost(Peer.getPeerUrl(peer));
        executeRequest(peerApi.addPeers(requestParameters));
    }

    public void sendUpdatedInfoToPeer(Peer peer, Peer currentPeer) {
        hostSelectionInterceptor.setHost(Peer.getPeerUrl(peer));
        executeRequest(peerApi.updatePeer(currentPeer));
    }

    public <T> T makeRequestToPeer(Peer peer, Call<T> request) {
        hostSelectionInterceptor.setHost(Peer.getPeerUrl(peer));
        return executeRequest(request).body();
    }

    public <T> retrofit2.Response<T> makeRequestToPeerWithResponse(Peer peer, Call<T> request) {
        hostSelectionInterceptor.setHost(Peer.getPeerUrl(peer));
        return executeRequest(request);
    }

    public <T> Map<Peer, T> makeRequestToAllPeers(Call<T> request, BooleanStatus onlineStatus,
                                                  BooleanStatus streamingStatus) {
        List<Peer> peers = peerService.getAllPeers(onlineStatus, streamingStatus);
        Map<Peer, T> peerResponses = new HashMap<>();
        Peer ownPeer = peerService.getOwnPeer();
        if (CollectionUtils.isNotEmpty(peers)) {
            peers.forEach(peer -> {
                if (!peerService.arePeersEqual(ownPeer, peer)) {
                    try {
                        T response = makeRequestToPeer(peer, request.clone());
                        peerResponses.put(peer, response);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            });
        }
        return peerResponses;
    }
    @Component
    public static class PeerNetworkService extends NetworkService {
        @Autowired
        PeerService peerService;
        @Override
        public String getApiUrl() {
            return Peer.getPeerUrl(peerService.getOwnPeer());
        }
    }
    public interface PeerApi {
        @POST("peers")
        Call<Void> addPeers(@Body Map<String, Object> queryParams);
        @PUT("peers")
        Call<Void> updatePeer(@Body Peer peer);
    }
}
