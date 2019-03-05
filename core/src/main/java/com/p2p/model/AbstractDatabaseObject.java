package com.p2p.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.p2p.constants.DBConstants;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractDatabaseObject implements Serializable {


    private static final long serialVersionUID = -1592816601882082371L;

    @Column(name = DBConstants.COLUMN_CREATED_TIME)
    private LocalDateTime createdTime;
    @JsonIgnore
    @Column(name = DBConstants.COLUMN_LAST_UPDATED_TIME)
    private LocalDateTime lastUpdatedTime;

    /**
     * Gets created time
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    /**
     * Sets created time.
     */
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * Gets last updated time.
     */
    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    /**
     * Sets last updated time.
     */
    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }
}
