package com.p2p.files.service;

import com.p2p.exceptions.CoreException;
import com.p2p.files.dao.UploadedFileChunkDao;
import com.p2p.files.dao.UploadedFileDao;
import com.p2p.files.models.FileChunkType;
import com.p2p.files.models.FileRange;
import com.p2p.files.models.UploadedFile;
import com.p2p.files.models.FileChunk;
import com.p2p.files.utils.FileIOUtils;
import com.p2p.model.BooleanStatus;
import com.p2p.peers.model.Peer;
import com.p2p.validations.NotEmptyList;
import net.sf.oval.constraint.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class will perform database query operations to save File Info and also handles
 * interaction with network
 */
@Service("uploadedFileService")
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Throwable.class)
public class UploadedFileService {

    private static final Logger LOG = Logger.getLogger(UploadedFileService.class);

    @Autowired
    private UploadedFileDao uploadedFileDao;
    @Autowired
    private UploadedFileChunkDao uploadedFileChunkDao;
    @Autowired
    private LocalFileStorage localFileStorage;
    @Autowired
    private FileIOUtils fileUtils;
    @Autowired
    private FileRestService fileRestService;

    /**
     * Create uploaded file string.. It will create entry into database
     */
    public String createUploadedFile(@NotNull(message = "uploaded file cannot be null") UploadedFile uploadedFile) {
        return (String) uploadedFileDao.create(uploadedFile);
    }

    public UploadedFile getUploadedFile(@NotNull(message = "uploaded file id cannot be null") String uploadedFileId) {
        UploadedFile file = uploadedFileDao.get(UploadedFile.class, uploadedFileId);
        if (file == null) {
            throw new CoreException.NotFoundException("file with id %s doesnt exist", uploadedFileId);
        }
        return file;
    }

    /**
     * Gets uploaded file by hash. It will query databse for UploadedFile using file hash
     *
     */
    public UploadedFile getUploadedFileByHash(@NotNull(message = "file hash cannot be ull") String fileHash) {
        UploadedFile file = uploadedFileDao.getFileByHash(fileHash);
        if (file == null) {
            throw new CoreException.NotFoundException("file with hash %s doesnt exist", fileHash);
        }
        return file;
    }

    /**
     * Gets all uploaded files. It will retrieve all the UploadedFile filtered by status from databse
     */
    public List<UploadedFile> getAllUploadedFiles(BooleanStatus status) {
        return uploadedFileDao.getAllFiles(status);
    }

    /**
     * Create uploaded file chunk string. Creates databse entry for FileChunk into databse
     *
     */
    public String createUploadedFileChunk(@NotNull(message = "uploaded file chunk cannot be null")
                                                  FileChunk fileChunk) throws IOException {

        if (fileChunk.getChunkType() == FileChunkType.UPLOADED) {
            // It means the chunk is created from index directory
            File file = getFile(fileChunk.getUploadedFile().getFilePath(), true);
            // Open input stream to the file handle to make chunk
            InputStream inputStream = FileUtils.openInputStream(file);
            FileRange fileRange = new FileRange(fileChunk.getFileOffset(), fileChunk.getSize());
            String outputChunkName = getDownloadedFilePath(RandomStringUtils.randomAlphabetic(5) + ".chunk");
            File outputFile = fileUtils.getFile(outputChunkName, true);
            OutputStream outputStream = FileUtils.openOutputStream(outputFile);
            // Reads the original file fileChunk.getFileOfset() and chunkSize and saves into a new file
            fileUtils.copyLarge(inputStream, outputStream, fileRange);
            closeSilently(outputStream, inputStream);
            fileChunk.setChunkPath(outputChunkName);
        } else {
            //It will just check if file exists
            getFile(fileChunk.getChunkPath(), true);
        }
        //Create entry into databsae
        return (String) uploadedFileChunkDao.create(fileChunk);
    }

    /**
     Get uploaded file chuunk from databse
     */
    public FileChunk getUploadedFileChunk(
            @NotNull(message = "uploaded file chunk id cannot be null") String uploadedFileChunkId) {
        FileChunk fileChunk = uploadedFileChunkDao.get(FileChunk.class, uploadedFileChunkId);
        if (fileChunk == null) {
            throw new CoreException.NotFoundException("file chunk with id %s doesnt exist", uploadedFileChunkId);
        }
        return fileChunk;
    }

    /**
     * Gets uploaded file chunk by hash.
     */
    public FileChunk getUploadedFileChunkByHash(
            @NotNull(message = "file hash cannot be null") String chunkHash) {
        FileChunk fileChunk = uploadedFileChunkDao.getFileChunkByHash(chunkHash, "uploadedFile");
        if (fileChunk == null) {
            throw new CoreException.NotFoundException("file chunk with hash %s doesnt exist", chunkHash);
        }
        return fileChunk;
    }

    /**
     * Gets uploaded file chunks by file.
     */
    public List<FileChunk> getUploadedFileChunksByFile(
            @NotNull(message = "uploaded file cannot be null") UploadedFile file) {
        return uploadedFileChunkDao.getFileChunksByFile(file, "uploadedFile");
    }

    /**
     * Gets uploaded files by hash.
     */
    public List<UploadedFile> getUploadedFilesByHash(
            @NotNull(message = "files list cannot be null") List<String> fileHashes) {
        return uploadedFileDao.getFilesByHash(fileHashes);
    }

    /**
     * Gets files by hash from network.
     */
    public Map<Peer, List<UploadedFile>> getFilesByHashFromNetwork(
            @NotEmptyList(message = "file list cannot be null") List<String> fileHashes, BooleanStatus status,
            BooleanStatus streamingStatus) {
        return fileRestService.getFilesByHashFromNetwork(fileHashes, status, streamingStatus);
    }

    /**
     * Gets files from direcotry.
     */
    public List<File> getFilesFromDirecotry(@NotNull(message = "directory cannot be null") File directory,
                                            IOFileFilter fileFilter, IOFileFilter directoryFilter) {
        return localFileStorage.listFiles(directory, fileFilter, directoryFilter);
    }

    /**
     This will take a directory as input and index all the files. here is where the database
     entries for the files in indexed directory is created
     */
    public List<UploadedFile> indexDirectory(@NotNull(message = "directory cannot be null") File directory,
                                             IOFileFilter fileFilter, IOFileFilter directoryFilter) {
        List<File> files = getFilesFromDirecotry(directory, fileFilter, directoryFilter);
        List<String> indexedFileHashes = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(files)) {
            LOG.error("Total files to be indexed " + files.size());
            files.forEach(file -> {
                try {
                    indexedFileHashes.add(addFileToIndex(file));
                } catch (IOException ioe) {
                    LOG.error(ioe.getMessage(), ioe);
                }
            });
        }
        return uploadedFileDao.getFilesByHash(indexedFileHashes);
    }


    /**
     * Will take individual file handle and create entry into datbase using the info given by File

     */
    public String addFileToIndex(@NotNull(message = "file cannot be null") File file) throws IOException {
        String fileHash = fileUtils.getFileMd5(file);
        UploadedFile uploadedFile = uploadedFileDao.getFileByHash(fileHash);
        LOG.error("Looking for file with hash " + fileHash);
        // If file doesnt exist in database it will create new entry. If it already exists will ignore
        if (uploadedFile == null) {
            // Creating UploadedFile object and filling details from file handle
            uploadedFile = new UploadedFile();
            uploadedFile.setFileName(file.getName());
            uploadedFile.setFilePath(file.getAbsolutePath());
            uploadedFile.setFileHash(fileHash);
            uploadedFile.setStatus(BooleanStatus.ACTIVE);
            uploadedFile.setFileSize(file.length());
            LOG.error("Adding file to index " + uploadedFile.getFilePath());
            createUploadedFile(uploadedFile);
            //Creating file chunks too
            splitFileToChunks(uploadedFile);
        } else {
            LOG.error("File with hash " + fileHash + " already exists");
        }
        return fileHash;
    }

    /**
     * Split file to chunks list.
     */
    public List<String> splitFileToChunks(UploadedFile uploadedFile) {
        File file = getFile(uploadedFile.getFilePath(), true);
        //Calculate the file ranges
        List<FileRange> fileRanges = fileUtils.getFileRanges(file);
        List<String> uploadedFileChunks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fileRanges)) {
            fileRanges.forEach(fileRange -> {
                try {
                    //Creatinig file chunk entries into databse
                    FileChunk fileChunk = new FileChunk();
                    fileChunk.setUploadedFile(uploadedFile);
                    fileChunk.setFileOffset(fileRange.getOffset());
                    fileChunk.setSize(fileRange.getSize());
                    fileChunk.setStatus(BooleanStatus.ACTIVE);
                    fileChunk.setChunkType(FileChunkType.UPLOADED);
                    fileChunk.setChunkHash(fileUtils.getMD5HashForRange(file, fileRange));
                    uploadedFileChunks.add(createUploadedFileChunk(fileChunk));
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                    throw new CoreException.NotValidException(e.getMessage(), e);
                }
            });
        }
        return uploadedFileChunks;
    }

    /**
     * Gets file chunk.
     */
    public File getFileChunk(FileChunk fileChunk) throws IOException {
        File file = getFile(fileChunk.getChunkPath(), true);
        return file;
    }

    /**
     * Gets uploaded file chunk from network.
     */
    public Map<Peer, FileChunk> getUploadedFileChunkFromNetwork(String chunkHash, BooleanStatus onlineStatus,
                                                                BooleanStatus streamingStatus) {
        return fileRestService.getUploadedFileChunkFromNetwork(chunkHash, onlineStatus, streamingStatus);
    }

    /**
      download the filechunk from peer save to the downloads directory
     * and return inputstream handle the file
     */
    public InputStream downloadUploadedFileChunk(FileChunk fileChunk, Peer peer) throws IOException {
        // downlaoding chunk from peer
        InputStream stream = fileRestService.downloadFileFromPeer(fileChunk.getChunkHash(), peer);
        // creating random name to save downloaded chunk
        String outputChunkName = getDownloadedFilePath(RandomStringUtils.randomAlphabetic(5) + ".chunk");
        File outputFile = fileUtils.getFile(outputChunkName, true);
        OutputStream outputStream = FileUtils.openOutputStream(outputFile);
        IOUtils.copyLarge(stream, outputStream);
        // sacing the chunk and closing the stream
        closeSilently(outputStream, stream);
        //creating databse entry
        FileChunk newFileChunk = new FileChunk();
        newFileChunk.setStatus(BooleanStatus.ACTIVE);
        newFileChunk.setFileOffset(fileChunk.getFileOffset());
        newFileChunk.setSize(fileChunk.getSize());
        newFileChunk.setChunkType(FileChunkType.DOWNLOADED);
        newFileChunk.setChunkPath(outputChunkName);
        newFileChunk.setChunkHash(fileChunk.getChunkHash());
        newFileChunk.setUploadedFile(null);
        newFileChunk.setFileChunkId(null);
        createUploadedFileChunk(newFileChunk);
        //returning input stream
        return FileUtils.openInputStream(outputFile);
    }

    /**
     * Gets uploaded file chunks by file hash.
     */
    public List<FileChunk> getUploadedFileChunksByFileHash(String fileHash, Peer peer) {
        return fileRestService.getUploadedFileChunksByFileHash(fileHash, peer);
    }

    /**
     * Gets uploaded file chunk by chunk hash.
     */
    public FileChunk getUploadedFileChunkByChunkHash(String chunkHash, Peer peer) {
        return fileRestService.getUploadedFileChunkByChunkHash(chunkHash, peer);
    }

    /**
     * Gets uploaded file chunks from file from network
     */
    public List<FileChunk> getUploadedFileChunksFromFileFromNetwork(String fileHash,
                                                                    BooleanStatus onlineStatus,
                                                                    BooleanStatus streamingStatus) {
        // Gets the file info using hashes from network. Will query all the peers with status onlineStatus and streamingStaus
        Map<Peer, List<UploadedFile>> uploadedFileMap =
                getFilesByHashFromNetwork(Arrays.asList(fileHash), onlineStatus, streamingStatus);
        Set<Map.Entry<Peer, List<UploadedFile>>> entrySet = uploadedFileMap.entrySet();
        if (CollectionUtils.isNotEmpty(entrySet)) {
            // here it is retrieving info from the first peer
            Map.Entry<Peer, List<UploadedFile>> uploadedFilePeerMap = IterableUtils.get(entrySet, 0);
            return getUploadedFileChunksByFileHash(fileHash, uploadedFilePeerMap.getKey());
        } else {
            throw new CoreException.NotFoundException("Unable to find file with hash " + fileHash);
        }
    }
    /**
     * Create a random file name for the file to be downloaded
     */
    public String createDownloadedFile(UploadedFile uploadedFile) {
        UploadedFile downloadedFile = new UploadedFile();
        downloadedFile.setFileSize(uploadedFile.getFileSize());
        downloadedFile.setFileHash(uploadedFile.getFileHash());
        downloadedFile.setFilePath(getDownloadedFilePath(uploadedFile.getFileName()));
        downloadedFile.setFileName(uploadedFile.getFileName());
        downloadedFile.setStatus(BooleanStatus.CREATED);
        return createUploadedFile(downloadedFile);
    }

    private String getDownloadedFilePath(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        String newFileName = FilenameUtils.getName(fileName);
        newFileName = newFileName + RandomStringUtils.randomAlphabetic(3) + "." + extension;
        return FilenameUtils
                .concat(getDownloadDirectory().getAbsolutePath(), newFileName);
    }

    /**
     internall function. will be used to filter the files in directory by extensions
     */
    public IOFileFilter getExtensionFilter(
            List<String> extensions) {
        if (CollectionUtils.isEmpty(extensions)) {
            return TrueFileFilter.INSTANCE;
        } else {
            return new SuffixFileFilter(extensions);
        }
    }

    public File getSharedDirectory() {
        return localFileStorage.getSharedDirectory();
    }

    public File getDownloadDirectory() {
        return localFileStorage.getDownloadDirectory();
    }

    public List<String> getRequiredExtensions() {
        return localFileStorage.getRequiredExtensions();
    }

    public File getFile(String filePath, boolean shouldExist) {
        File file = fileUtils.getFile(filePath);
        if (shouldExist && (file == null || !file.exists())) {
            throw new CoreException.NotFoundException("file doesnt exist at path " + filePath);
        }
        return file;
    }

    public void closeSilently(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            fileUtils.closeSilently(closeable);
        }
    }

    public File createFile(UploadedFile uploadedFile) throws IOException {
        return fileUtils.getFile(uploadedFile.getFilePath(), true);
    }
}
