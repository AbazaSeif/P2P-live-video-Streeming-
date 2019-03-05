package com.p2p.files.utils;

import com.p2p.exceptions.CoreException;
import com.p2p.files.models.FileRange;
import com.p2p.files.service.config.FilesConfiguration;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Component("fileIOUtils")
public class FileIOUtils {

    @Autowired
    private FilesConfiguration filesConfiguration;
    private static final Logger LOG = Logger.getLogger(FileIOUtils.class);

    public File getFile(String fileName, boolean shouldCreate) throws IOException {
        File file = FileUtils.getFile(fileName);
        if (!file.exists() && shouldCreate) {
            file.createNewFile();
        }
        return file;
    }

    public File getDirectory(String directory) throws IOException {
        File file = getFile(directory, true);
        if (!file.isDirectory()) {
            throw new CoreException.NotValidException("directory name expected. But file name given " + directory);
        }
        return file;
    }

    public long getFileSize(File file) {
        return FileUtils.sizeOf(file);
    }

    public long decideChunkSize(long fileSize, int chunks) {
        return fileSize / chunks;
    }

    public List<FileRange> calculateFileRanges(long fileSize, long chunkSize) {
        List<FileRange> fileRanges = new ArrayList<>();
        fileRanges.add(new FileRange(0, filesConfiguration.firstChunkSize));
        for (long currentOffset = filesConfiguration.firstChunkSize; currentOffset < fileSize; ) {
            long currentChunkSize = currentOffset + chunkSize > fileSize ? fileSize - currentOffset : chunkSize;
            fileRanges.add(new FileRange(currentOffset, currentChunkSize));
            currentOffset = currentOffset + currentChunkSize;
        }
        return fileRanges;
    }

    public List<FileRange> getFileRanges(File file) {
        long fileSize = getFileSize(file);
        long chunkSize = decideChunkSize(fileSize, filesConfiguration.minChunks);
        return calculateFileRanges(fileSize, chunkSize);
    }

    public File getFile(String fileName) {
        return FileUtils.getFile(fileName);
    }

    public String getFileMd5(File file) throws IOException {
        InputStream stream = FileUtils.openInputStream(file);
        String md5 = getFileMd5(stream);
        closeSilently(stream);
        return md5;
    }

    public String getFileMd5(InputStream inputStream) throws IOException {
        return DigestUtils.md5Hex(inputStream);
    }

    public void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {

        }
    }

    public String getMD5HashForRange(File file, FileRange fileRange) throws IOException {
        File temporaryFile = getTemporaryFile(file, fileRange);
        String fileHash = getFileMd5(temporaryFile);
        FileUtils.forceDelete(temporaryFile);
        return fileHash;
    }

    public File getTemporaryFile(File file, FileRange fileRange) throws IOException {
        File temporaryFile = File.createTempFile(file.getName(), RandomStringUtils.randomAlphanumeric(5));
        OutputStream temporaryStream = FileUtils.openOutputStream(temporaryFile);
        InputStream inputStream = FileUtils.openInputStream(file);
        long copiedBytes = copyLarge(inputStream, temporaryStream, fileRange);
        closeSilently(inputStream);
        closeSilently(temporaryStream);
        return temporaryFile;
    }

    public long copyLarge(InputStream inputStream, OutputStream outputStream, FileRange fileRange)
            throws IOException {
        return IOUtils
                .copyLarge(inputStream, outputStream, fileRange.getOffset(), fileRange.getSize());
    }

    public List<File> splitFile(File file, String outputDirectory, List<FileRange> fileRanges) throws IOException {
        String fileName = FilenameUtils.getName(file.getAbsolutePath());
        List<File> outputFiles = new ArrayList<>();
        for (int i = 0; i < fileRanges.size(); i++) {

            InputStream fileInputStream = FileUtils.openInputStream(file);
            String chunkedFileName = getChunkedFileName(fileName, i + 1);
            File outputChunk = FileUtils.getFile(FilenameUtils.concat(outputDirectory, chunkedFileName));
            OutputStream chunkOutputStream = FileUtils.openOutputStream(outputChunk);
            copyLarge(fileInputStream, chunkOutputStream, fileRanges.get(i));
            closeSilently(chunkOutputStream);
            closeSilently(fileInputStream);
            outputFiles.add(outputChunk);
        }
        return outputFiles;
    }

    public void joinFiles(List<File> files, File outputFile) throws IOException {
        FileOutputStream outputStream = FileUtils.openOutputStream(outputFile, true);
        files.forEach(file -> {
            try {
                FileUtils.writeByteArrayToFile(outputFile, FileUtils.readFileToByteArray(file), true);
            } catch (IOException e) {
                throw new CoreException.NotValidException(e.getMessage(), e);
            }
        });
        closeSilently(outputStream);
    }

    private String getChunkedFileName(String fileName, int chunkNumber) {
        return fileName + "." + chunkNumber;
    }
}
