package com.p2p.peers.service;

import java.util.List;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:peers.properties")
public class PeerConfiguration {

    @Value("#{'${com.peer.knownpeers}'.split(',')}")
    List<String> knownPeers;

    @Value("#{'${com.peer.ip}'}")
    String currentPeerIp;

    @Value("#{'${com.peer.port}'}")
    String currentPeerPort;

    @Value("#{'${com.peer.name}'}")
    String currentPeerName;

    public Peer getOwnPeer() {
        Peer peer = new Peer();
        peer.setName(currentPeerName);
        peer.setIp(currentPeerIp);
        peer.setPort(currentPeerPort);
        peer.setOnline(BooleanStatus.ACTIVE);
        peer.setStreaming(BooleanStatus.DISABLED);

        return peer;
    }

}
