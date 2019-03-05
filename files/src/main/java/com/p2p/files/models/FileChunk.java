package com.p2p.files.models;

import com.p2p.constants.DBConstants;
import com.p2p.files.dao.FileDBConstants;
import com.p2p.model.AbstractDatabaseObject;
import com.p2p.model.BooleanStatus;
import com.p2p.model.marshaller.BooleanStatusMarshaller;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The type Uploaded file chunk.
 */
@Entity
@Table(name = FileDBConstants.TABLE_FILE_CHUNKS)
public class FileChunk extends AbstractDatabaseObject {

    @Id
    @Column(name = FileDBConstants.COLUMN_FILE_CHUNKS_ID)
    @GeneratedValue(generator = DBConstants.HIBERNATE_UUID_GENERATOR)
    @GenericGenerator(name = DBConstants.HIBERNATE_UUID_GENERATOR,
            strategy = DBConstants.HIBERNATE_UUID_GENERATOR_STRATEGY)
    private String fileChunkId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FileDBConstants.COLUMN_FILE_CHUNKS_FILE_ID,
            referencedColumnName = FileDBConstants.COLUMN_FILES_ID)
    private UploadedFile uploadedFile;
    @Column(name = FileDBConstants.COLUMN_FILE_CHUNKS_MD5_HASH)
    private String chunkHash;
    @Column(name = FileDBConstants.COLUMN_FILE_CHUNKS_OFFSET)
    private Long fileOffset;
    @Column(name = FileDBConstants.COLUMN_FILE_CHUNKS_TYPE)
    @Convert(converter = FileChunkTypeMarshaller.class)
    private FileChunkType chunkType;
    @Column(name = FileDBConstants.COLUMN_FILE_CHUNKS_LENGTH)
    private Long size;
    @Column(name = FileDBConstants.COLUMN_FILE_CHUNKS_PATH)
    private String chunkPath;
    @Column(name = FileDBConstants.COLUMN_FILE_CHUNKS_STATUS)
    @Convert(converter = BooleanStatusMarshaller.class)
    private BooleanStatus status;

    public String getFileChunkId() {
        return fileChunkId;
    }
    public void setFileChunkId(String fileChunkId) {
        this.fileChunkId = fileChunkId;
    }
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    public Long getFileOffset() {
        return fileOffset;
    }
    public void setFileOffset(Long fileOffset) {
        this.fileOffset = fileOffset;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }
    public BooleanStatus getStatus() {
        return status;
    }
    public void setStatus(BooleanStatus status) {
        this.status = status;
    }
    public String getChunkHash() {
        return chunkHash;
    }
    public void setChunkHash(String chunkHash) {
        this.chunkHash = chunkHash;
    }
    public FileChunkType getChunkType() {
        return chunkType;
    }
    public void setChunkType(FileChunkType chunkType) {
        this.chunkType = chunkType;
    }
    public String getChunkPath() {
        return chunkPath;
    }
    public void setChunkPath(String chunkPath) {
        this.chunkPath = chunkPath;
    }
}
