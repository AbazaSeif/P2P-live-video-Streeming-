package com.p2p.files.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.p2p.constants.DBConstants;
import com.p2p.files.dao.FileDBConstants;
import com.p2p.model.AbstractDatabaseObject;
import com.p2p.model.BooleanStatus;
import com.p2p.model.marshaller.BooleanStatusMarshaller;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The type Uploaded file.
 */
@Entity
@Table(name = FileDBConstants.TABLE_FILES,
        uniqueConstraints = {@UniqueConstraint(columnNames = {FileDBConstants.COLUMN_FILES_MD5_HASH})})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UploadedFile extends AbstractDatabaseObject {

    @Id
    @Column(name = FileDBConstants.COLUMN_FILES_ID)
    @GeneratedValue(generator = DBConstants.HIBERNATE_UUID_GENERATOR)
    @GenericGenerator(name = DBConstants.HIBERNATE_UUID_GENERATOR,
            strategy = DBConstants.HIBERNATE_UUID_GENERATOR_STRATEGY)
    private String fileId;
    @Column(name = FileDBConstants.COLUMN_FILES_NAME)
    private String fileName;
    @Column(name = FileDBConstants.COLUMN_FILES_PATH)
    private String filePath;
    @Column(name = FileDBConstants.COLUMN_FILES_SIZE)
    private Long fileSize;
    @Column(name = FileDBConstants.COLUMN_FILES_MD5_HASH)
    private String fileHash;
    @Column(name = FileDBConstants.COLUMN_FILES_STATUS)
    @Convert(converter = BooleanStatusMarshaller.class)
    private BooleanStatus status;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public BooleanStatus getStatus() {
        return status;
    }
    
    public void setStatus(BooleanStatus status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
