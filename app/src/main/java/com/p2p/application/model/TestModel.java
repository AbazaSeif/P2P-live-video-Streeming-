package com.p2p.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import com.p2p.constants.DBConstants;

@Entity
@Table(name = "test_table")
public class TestModel {

    @Id
    @GeneratedValue(generator = DBConstants.HIBERNATE_UUID_GENERATOR)
    @GenericGenerator(name = DBConstants.HIBERNATE_UUID_GENERATOR, strategy = DBConstants.HIBERNATE_UUID_GENERATOR_STRATEGY)
    @Column(name = "id")
    private String userId;
    @Column(name = "name")
    private String userName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
