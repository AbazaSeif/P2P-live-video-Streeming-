package com.p2p.peers.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.p2p.model.BooleanStatus;
import com.p2p.validations.NotEmptyList;
import com.p2p.validations.NotEmptyString;
import net.sf.oval.constraint.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.p2p.exceptions.CoreException;
import com.p2p.peers.dao.PeerDao;
import com.p2p.peers.model.Peer;
import com.p2p.utils.DaoUtils;
import javax.annotation.PreDestroy;

@Service("peerService")
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Throwable.class)
public class PeerService {

    private static final Logger LOG = LoggerFactory.getLogger(PeerService.class);
    @Autowired
    private PeerDao peerDao;
    @Autowired
    private DaoUtils daoUtils;
    @Autowired
    private PeerConfiguration peerConfiguration;
    @Autowired
    private PeerRestService peerRestService;

    /**
     * Update current peer.
     */
    public void updateCurrentPeer() {
        createOrUpdatePeer(peerConfiguration.getOwnPeer());
    }

    public void updateKnownPeers() throws MalformedURLException {
        List<String> knownPeers = peerConfiguration.knownPeers;
        if (CollectionUtils.isNotEmpty(knownPeers)) {
            for (String peerAddress : knownPeers) {
                Peer peer = new Peer();
                URL url = new URL(peerAddress);
                peer.setIp(url.getHost());
                peer.setPort(String.valueOf(url.getPort()));
                try {
                    createOrUpdatePeer(peer);
                } catch (Exception e) {
                    LOG.info(e.getMessage());
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void createOrUpdatePeer(Peer peer) {
        try {
            Peer existingPeer = getPeerByIpAndPort(peer.getIp(), peer.getPort());
            existingPeer.setName(peer.getName());
            existingPeer.setOnline(peer.getOnline());
            existingPeer.setStreaming(peer.getStreaming());
            peer = existingPeer;
        } catch (Exception ne) {
            peer.setPeerId(RandomStringUtils.randomAlphabetic(32));
        }
        peerDao.saveOrUpdate(peer);
    }

    public List<Peer> getAllPeers(BooleanStatus onlineStatus, BooleanStatus streamingStatus) {
        return peerDao.getAllPeers(onlineStatus, streamingStatus);
    }

    public boolean arePeersEqual(@NotNull(message = "peer cannot be null") Peer peer1,
                                 @NotNull(message = "peer cannot be null") Peer peer2) {
        return StringUtils.equals(peer1.getIp(), peer2.getIp()) &&
                StringUtils.equals(peer1.getPort(), peer2.getPort()) &&
                StringUtils.equals(peer1.getName(), peer2.getName())
                && peer1.getOnline() == peer2.getOnline() && peer1.getStreaming() == peer2.getStreaming();
    }

    public void sendNewPeersToPeer(@NotNull(message = "peer cannot be null") Peer peer,
                                   @NotEmptyList(message = "peers list cannot be empty") List<Peer> peers) {
        try {
            peerRestService.sendNewPeersToPeer(peer, peers);
        } catch (Exception e) {
            markPeerOffline(peer);
            throw e;
        }
    }

    public void sendUpdatedInfoToPeer(@NotNull(message = "peer cannot be null") Peer peer,
                                      @NotNull(message = "updated peer cannot be null") Peer updatedPeer) {
        try {
            peerRestService.sendUpdatedInfoToPeer(peer, updatedPeer);
        } catch (Exception e) {
            markPeerOffline(peer);
            throw e;
        }
    }

    public void markPeerOffline(@NotNull(message = "peer cannot be null") Peer peer) {
        LOG.error("Peer " + peer + " throwed error. Marking it offline");
        peer.setOnline(BooleanStatus.DISABLED);
        createOrUpdatePeer(peer);
    }

    public void sendNewPeersToAllPeers(
            @NotEmptyList(message = "updated peers should not be empty") List<Peer> updatedPeers,
            BooleanStatus onlineStatus, BooleanStatus streamingStatus) {
        List<Peer> allPeers = getAllPeers(onlineStatus, streamingStatus);
        if (CollectionUtils.isNotEmpty(allPeers)) {
            allPeers.forEach(peer -> {
                try {
                    LOG.info("Sending new peers info to peer " + peer);
                    sendNewPeersToPeer(peer, updatedPeers);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            });
        }
    }

    public void sendShutdownSignalToAllPeers(Peer currentPeer, BooleanStatus onlineStatus,
                                             BooleanStatus streamingStatus) {
        List<Peer> allPeers = getAllPeers(onlineStatus, streamingStatus);
        if (CollectionUtils.isNotEmpty(allPeers)) {
            allPeers.forEach(peer -> {
                try {
                    LOG.info("Sending peers info to peer " + peer);
                    sendUpdatedInfoToPeer(peer, currentPeer);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            });
        }
    }

    public List<Peer> getUpdatedPeers(List<Peer> givenPeers) {
        List<Peer> updatedPeers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(givenPeers)) {
            givenPeers.forEach(peer -> {
                try {
                    Peer existingPeer = getPeerByIpAndPort(peer);
                    if (!arePeersEqual(existingPeer, peer)) {
                        updatedPeers.add(peer);
                    }
                } catch (Exception e) {
                    updatedPeers.add(peer);
                }
            });
        }
        return updatedPeers;
    }

    public Peer getPeerByIpAndPort(@NotEmptyString(message = "ip cannot be emtpy") String ip,
                                   @NotEmptyString(message = "port cannot be empty") String port) {
        Peer peer = peerDao.getPeerByIpAndPort(ip, port);
        if (peer == null) {
            throw new CoreException.NotFoundException("peer doesnt exist with id %s and port %s", ip, port);
        }
        return peer;
    }

    public Peer getPeerByIpAndPort(@NotNull(message = "peer cannot be null") Peer peer) {
        return getPeerByIpAndPort(peer.getIp(), peer.getPort());
    }

    public Peer getOwnPeer() {
        Peer peer = peerConfiguration.getOwnPeer();
        Peer finalPeer = null;
        try {
            finalPeer = getPeerByIpAndPort(peer);
        } catch (Exception e) {
            createOrUpdatePeer(peer);
            finalPeer = peer;
        }
        return finalPeer;
    }

    @PreDestroy
    public void sendShutdownSignalToAllPeers() {
        Peer peer = getOwnPeer();
        peer.setStreaming(BooleanStatus.DISABLED);
        peer.setOnline(BooleanStatus.DISABLED);
        sendShutdownSignalToAllPeers(peer, BooleanStatus.ACTIVE, null);
    }
}
