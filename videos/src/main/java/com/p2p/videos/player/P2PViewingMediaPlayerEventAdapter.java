package com.p2p.videos.player;

import com.p2p.videos.model.VideoStream;
import uk.co.caprica.vlcj.binding.internal.libvlc_marquee_position_e;
import uk.co.caprica.vlcj.player.Marquee;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import javax.swing.*;
import java.awt.*;

public class P2PViewingMediaPlayerEventAdapter extends MediaPlayerEventAdapter {

    private VideoStream videoStream;

    public P2PViewingMediaPlayerEventAdapter(VideoStream videoStream) {
        this.videoStream = videoStream;
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        addText(videoStream.getVideoStreamName() + " Stream Finished", mediaPlayer);
    }


    @Override
    public void error(MediaPlayer mediaPlayer) {
        addText(videoStream.getVideoStreamName() + " Stream Error", mediaPlayer);
    }

    private void addText(String text, MediaPlayer mediaPlayer) {
        JOptionPane.showMessageDialog(null, text);
    }

}
