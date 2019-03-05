package com.p2p.files.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
public class LocalFileStorage {

    private static final Logger LOG = Logger.getLogger(LocalFileStorage.class);
    @Value("${com.p2p.files.sharedDirectory}")
    private String sharedDirectory;
    @Value("${com.p2p.files.downloadDirectory}")
    private String downloadDirectory;
    @Value("#{'${com.p2p.files.extensions}'.split(',')}")
    List<String> requiredExtensions;

    private File createFile(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        return new File(fileName);
    }

    /**
     * It will filter out the files given by fileFilter and directoryFileter
     */
    public List<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter directorFilter) {
        return new ArrayList<>(FileUtils.listFiles(directory, fileFilter, directorFilter));
    }

    public File getSharedDirectory() {
        return FileUtils.getFile(sharedDirectory);
    }

    public File getDownloadDirectory() {
        return FileUtils.getFile(downloadDirectory);
    }

    public List<String> getRequiredExtensions() {
        LOG.error(requiredExtensions);
        return requiredExtensions;
    }
}
