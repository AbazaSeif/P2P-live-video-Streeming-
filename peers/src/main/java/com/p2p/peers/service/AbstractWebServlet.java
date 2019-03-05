package com.p2p.peers.service;

import com.p2p.peers.model.Peer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbstractWebServlet {

    public <T> Map<String, List<T>> convertPeerMapToPeerUrlMap(Map<Peer, List<T>> peerListMap) {
        return peerListMap.entrySet().stream()
                .collect(Collectors.toMap(e -> Peer.getPeerUrl(e.getKey()), Map.Entry::getValue));
    }
}
