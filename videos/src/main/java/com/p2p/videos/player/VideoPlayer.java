package com.p2p.videos.player;

import com.p2p.files.utils.FileIOUtils;
import com.p2p.videos.model.VideoStream;
import com.p2p.videos.service.config.VideoConfiguration;
import com.sun.jna.NativeLibrary;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;
import uk.co.caprica.vlcj.player.media.Media;
import uk.co.caprica.vlcj.player.media.callback.seekable.RandomAccessFileMedia;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Arrays;

@Component
public class VideoPlayer {

    private VideoConfiguration videoConfiguration;

    private static final Logger LOG = Logger.getLogger(VideoPlayer.class);
    private static final String NATIVE_LIBRARY_SEARCH_PATH = "C:\\Program Files\\VideoLAN\\VLC";
    private JFrame frame;
    private EmbeddedMediaPlayerComponent mediaPlayerComponent;

    public VideoPlayer(VideoConfiguration videoConfiguration) {
        this();
        this.videoConfiguration = videoConfiguration;
    }

    private VideoPlayer() {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
        System.out.println(LibVlc.INSTANCE.libvlc_get_version());
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    }

    public void startPlayer(File file, MediaPlayerEventAdapter mediaPlayerEventAdapter) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createMediaPlayerComponent(file.getName(), mediaPlayerComponent);
                Media media = new RandomAccessFileMedia(file);
                MediaPlayer mediaPlayer = mediaPlayerComponent.getMediaPlayer();
                mediaPlayer.addMediaPlayerEventListener(mediaPlayerEventAdapter);
                mediaPlayer.playMedia(media);
            }
        });
    }

    public void createMediaPlayerComponent(String title, EmbeddedMediaPlayerComponent mediaPlayerComponent) {
        frame = new JFrame(title);
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(mediaPlayerComponent);
        mediaPlayerComponent.getMediaPlayer().setRate(videoConfiguration.playBackRate);
        frame.addWindowListener(getOnclosingWindowListener(mediaPlayerComponent.getMediaPlayer()));
        frame.setVisible(true);
    }

    private WindowListener getOnclosingWindowListener(MediaPlayer mediaPlayer) {
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayer.stop();
            }
        };
        return exitListener;
    }
}
