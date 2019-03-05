package com.p2p.gui.controller;

import com.p2p.gui.pages.AbstractPanel;
import com.p2p.gui.pages.AvailableStreamsPanel;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.videos.model.VideoStream;
import com.p2p.videos.service.VideoStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AvailableStreamsPanelController extends AbstractPanelController {

    @Autowired
    private VideoStreamService videoStreamService;

    @Override
    public String getName() {
        return "Available Streams Panel";
    }

    @Override
    public AbstractPanel getPanel() {
        Map<Peer, List<VideoStream>> videoStreamMap =
                videoStreamService.getStreamsFromNetwork(null, BooleanStatus.ACTIVE, BooleanStatus.ACTIVE, null);
        return new AvailableStreamsPanel(videoStreamMap);
    }
}
