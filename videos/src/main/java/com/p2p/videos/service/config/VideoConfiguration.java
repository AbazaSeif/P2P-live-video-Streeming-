package com.p2p.videos.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VideoConfiguration {

    @Value("#{'${com.p2p.video.predownloadchunks}'}")
    public int preDownloadChunks;
    @Value("#{'${com.p2p.video.playbackrate}'}")
    public int playBackRate;
}
