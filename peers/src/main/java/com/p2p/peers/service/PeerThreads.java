package com.p2p.peers.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import com.p2p.model.BooleanStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.p2p.peers.model.Peer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@EnableScheduling
@Component
public class PeerThreads {
    private static final Logger LOG = LoggerFactory.getLogger(PeerThreads.class);
    @Autowired
    private PeerService peerService;
    @Autowired
    private PeerConfiguration peerConfiguration;

    /**
     * Update known peers.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void updateKnownPeers() throws MalformedURLException, URISyntaxException {
        List<String> knownPeers = peerConfiguration.knownPeers;
        if (CollectionUtils.isNotEmpty(knownPeers)) {
            for (String peerAddress : knownPeers) {
                Peer peer = new Peer();
                URL url = new URL(peerAddress);
                peer.setIp(url.getHost());
                peer.setPort(String.valueOf(url.getPort()));
                peer.setOnline(BooleanStatus.ACTIVE);
                MultiValueMap<String, String> parameters =
                        UriComponentsBuilder.fromUriString(peerAddress).build().getQueryParams();
                peer.setName(ObjectUtils.defaultIfNull(parameters.getFirst("name"), Peer.getPeerUrl(peer)));
                try {
                    peerService.createOrUpdatePeer(peer);
                } catch (Exception e) {
                    LOG.info(e.getMessage(), e);
                }
            }
        }
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000000)
    public void sendPeerOnlineMessage() {
        peerService.sendNewPeersToAllPeers(Arrays.asList(peerService.getOwnPeer()), BooleanStatus.ACTIVE, null);
    }
}