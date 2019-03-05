package com.p2p.files.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilesConfiguration {
    @Value("#{'${com.p2p.files.minchunks}'}")
    public int minChunks;
    @Value("#{'${com.p2p.files.firstchunksize}'}")
    public int firstChunkSize;
}
