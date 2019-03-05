package com.p2p.peers.service;

import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.utils.ParameterParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The type Peer controller.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/peers")
public class PeerController {

    private static final Logger LOG = Logger.getLogger(PeerController.class);

    @Autowired
    private PeerService peerService;
    @Autowired
    private ParameterParser parameterParser;
    @Autowired
    private TaskScheduler scheduler;

    /**
     * Add peers.
     * @param requestParameters the request parameters
     */
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public void addPeers(@RequestBody Map<String, Object> requestParameters) {
        List<Map<String, Object>> peerValues = parameterParser.getListMapParameter(requestParameters, "peers", true);
        List<Peer> givenPeers = new ArrayList<>();
        peerValues.forEach(peerRequestParameters -> {
            givenPeers.add(getPeerFromRequestParameters(peerRequestParameters));
        });
        List<Peer> updatedPeers = peerService.getUpdatedPeers(givenPeers);
        if (CollectionUtils.isNotEmpty(updatedPeers)) {
            updatedPeers.forEach(peer -> {
                try {
                    peerService.createOrUpdatePeer(peer);
                } catch (Exception e) {
                    LOG.error("Unable to update peer " + Peer.getPeerUrl(peer));
                }
            });
            peerService.sendNewPeersToAllPeers(updatedPeers, BooleanStatus.ACTIVE, null);
        }
    }

    /**
     * Gets peers.
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Peer> getPeers(@RequestParam(name = "online", required = false) String onlineStatusString,
                               @RequestParam(name = "streaming", required = false) String streamingStatusString) {
        BooleanStatus onlineStatus = parameterParser
                .getEnumTypeFromString(onlineStatusString, "online", BooleanStatus.class, false);
        BooleanStatus streamingStatus = parameterParser
                .getEnumTypeFromString(streamingStatusString, "streaming", BooleanStatus.class, false);
        return peerService.getAllPeers(onlineStatus, streamingStatus);
    }

    /**
     * Update peer.
     */
    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public void updatePeer(@RequestBody Map<String, Object> requestParameters) {
        Peer updatedPeer = getPeerFromRequestParameters(requestParameters);
        List<Peer> updatedPeers = peerService.getUpdatedPeers(Arrays.asList(updatedPeer));
        if (CollectionUtils.isNotEmpty(updatedPeers)) {
            peerService.createOrUpdatePeer(updatedPeer);
        }
    }

    private Peer getPeerFromRequestParameters(Map<String, Object> requestParameters) {
        String ip = parameterParser.getStringParameter(requestParameters, "ip", true);
        String port = parameterParser.getStringParameter(requestParameters, "port", true);
        String name = parameterParser.getStringParameter(requestParameters, "name", false);
        BooleanStatus onlineStatus =
                parameterParser.getEnumTypeFromString(requestParameters, "online", BooleanStatus.class, false);
        BooleanStatus streamingStatus = parameterParser
                .getEnumTypeFromString(requestParameters, "streaming", BooleanStatus.class, false);
        Peer peer = new Peer();
        peer.setIp(ip);
        peer.setPort(port);
        peer.setName(name);
        peer.setOnline(onlineStatus);
        peer.setStreaming(streamingStatus);
        return peer;
    }

    @RequestMapping(value = "/streaming", produces = "text/event-stream")
    public ResponseBodyEmitter handleRequest() {

        final ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    LOG.error("Peer " + System.currentTimeMillis());
                    emitter.send(System.currentTimeMillis() + " - ", MediaType.TEXT_PLAIN);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }, 1000);
        return emitter;
    }
}