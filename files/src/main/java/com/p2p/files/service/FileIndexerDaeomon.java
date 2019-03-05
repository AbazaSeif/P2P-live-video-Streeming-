package com.p2p.files.service;

import com.p2p.utils.DateTimeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * The type File indexer daeomon.
 */
@Component
public class FileIndexerDaeomon {

    private static final Logger LOG = Logger.getLogger(FileIndexerDaeomon.class);

    private static final long FILE_CHECK_DAEMON_INTERVAL_SECONDS = 120;
    private static final long FILE_CHECK_DAEMON_INTERVAL_DELAY = 120;
    private static final long CONSTANT_THOUSAND = 1000;


    @Autowired
    private UploadedFileService uploadedFileService;
    @Autowired
    private DateTimeUtils dateTimeUtils;

    /**
     This method will execute peridically for every two months and identifies if there are any
     * new files added to the sharing directory. If a new file is added it will index the file and add it to databse
     */
    @Scheduled(initialDelay = FILE_CHECK_DAEMON_INTERVAL_DELAY,
            fixedDelay = FILE_CHECK_DAEMON_INTERVAL_SECONDS * CONSTANT_THOUSAND)
    public void indexSharedDirectory() {
        File sharedDirectory = uploadedFileService.getSharedDirectory();
        LOG.info("Indexing directory " + sharedDirectory.getAbsolutePath() + " at time " +
                dateTimeUtils.getApplicationCurrentTime());
        uploadedFileService.indexDirectory(sharedDirectory,
                uploadedFileService.getExtensionFilter(uploadedFileService.getRequiredExtensions()),
                TrueFileFilter.INSTANCE);
    }
}
