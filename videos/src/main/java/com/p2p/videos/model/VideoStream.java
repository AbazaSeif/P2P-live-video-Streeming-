package com.p2p.videos.model;

import com.p2p.constants.DBConstants;
import com.p2p.files.dao.FileDBConstants;
import com.p2p.files.models.UploadedFile;
import com.p2p.model.AbstractDatabaseObject;
import com.p2p.model.BooleanStatus;
import com.p2p.model.marshaller.BooleanStatusMarshaller;
import com.p2p.videos.dao.VideoDBConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = VideoDBConstants.TABLE_VIDEO_STREAMS)
public class VideoStream extends AbstractDatabaseObject {

    @Id
    @Column(name = VideoDBConstants.COLUMN_VIDEO_STREAM_ID)
    @GeneratedValue(generator = DBConstants.HIBERNATE_UUID_GENERATOR)
    @GenericGenerator(name = DBConstants.HIBERNATE_UUID_GENERATOR,
            strategy = DBConstants.HIBERNATE_UUID_GENERATOR_STRATEGY)
    private String videoStreamId;
    @Column(name = VideoDBConstants.COLUMN_VIDEO_STREAM_NAME)
    private String videoStreamName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = VideoDBConstants.COLUMN_VIDEO_STREAM_FILE,
            referencedColumnName = FileDBConstants.COLUMN_FILES_ID)
    private UploadedFile uploadedFile;
    @Column(name = VideoDBConstants.COLUMN_VIDEO_STREAM_CHUNKS)
    private Integer chunks;
    @Column(name = VideoDBConstants.COLUMN_VIDEO_STREAM_DURATION)
    private Long duration;
    @Column(name = VideoDBConstants.COLUMN_VIDEO_STREAM_STATUS)
    @Convert(converter = BooleanStatusMarshaller.class)
    private BooleanStatus status;

    public String getVideoStreamId() {
        return videoStreamId;
    }

    public void setVideoStreamId(String videoStreamId) {
        this.videoStreamId = videoStreamId;
    }

    public String getVideoStreamName() {
        return videoStreamName;
    }

    public void setVideoStreamName(String videoStreamName) {
        this.videoStreamName = videoStreamName;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public BooleanStatus getStatus() {
        return status;
    }

    public void setStatus(BooleanStatus status) {
        this.status = status;
    }
}
