package com.p2p.videos.player;

import com.p2p.model.BooleanStatus;
import com.p2p.videos.model.VideoStream;
import com.p2p.videos.service.VideoStreamService;
import uk.co.caprica.vlcj.binding.internal.libvlc_marquee_position_e;
import uk.co.caprica.vlcj.player.Marquee;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import javax.swing.*;
import java.awt.*;

public class P2PStreamingMediaPlayerEventAdapter extends MediaPlayerEventAdapter {

    private VideoStream videoStream;
    private VideoStreamService videoStreamService;

    public P2PStreamingMediaPlayerEventAdapter(VideoStream videoStream, VideoStreamService videoStreamService) {
        this.videoStream = videoStream;
        this.videoStreamService = videoStreamService;
    }

    @Override
    public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
        if (newLength > 0) {
            videoStream.setDuration(newLength);
            videoStream.setStatus(BooleanStatus.ACTIVE);
            videoStreamService.updateVideoStream(videoStream);
        }
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {

    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        addText("Stream finished", mediaPlayer);
        finishStream();
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        addText("Stream error. Close window and try again", mediaPlayer);
        finishStream();
    }

    private void finishStream() {
        videoStream.setStatus(BooleanStatus.COMPLETED);
        videoStreamService.updateVideoStream(videoStream);
    }

    private void addText(String text, MediaPlayer mediaPlayer) {
        JOptionPane.showMessageDialog(null, text);
    }
}
