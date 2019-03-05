package com.p2p.peers.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.p2p.constants.DBConstants;
import com.p2p.model.AbstractDatabaseObject;
import com.p2p.model.BooleanStatus;
import com.p2p.model.marshaller.BooleanStatusMarshaller;
import com.p2p.peers.dao.PeerDBConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import static com.p2p.peers.dao.PeerDBConstants.COLUMN_PEER_IP;
import static com.p2p.peers.dao.PeerDBConstants.COLUMN_PEER_PORT;

/**
 * The type Peer.
 */
@Entity
@Table(name = PeerDBConstants.TABLE_PEERS,
        uniqueConstraints = {@UniqueConstraint(columnNames = {COLUMN_PEER_IP, COLUMN_PEER_PORT})})
public class Peer extends AbstractDatabaseObject {

    private static final long serialVersionUID = 2796905595067344770L;

    @Id
    @Column(name = PeerDBConstants.COLUMN_PEER_ID)
    private String peerId;
    @Column(name = PeerDBConstants.COLUMN_PEER_NAME)
    private String name;
    @Column(name = COLUMN_PEER_IP)
    private String ip;
    @Column(name = PeerDBConstants.COLUMN_PEER_PORT)
    private String port;
    @Column(name = PeerDBConstants.COLUMN_PEER_ONLINE)
    @Convert(converter = BooleanStatusMarshaller.class)
    private BooleanStatus online;
    @Column(name = PeerDBConstants.COLUMN_PEER_STREAMING)
    @Convert(converter = BooleanStatusMarshaller.class)
    private BooleanStatus streaming;

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public BooleanStatus getOnline() {
        return online;
    }

    public void setOnline(BooleanStatus online) {
        this.online = online;
    }

    public BooleanStatus getStreaming() {
        return streaming;
    }

    public void setStreaming(BooleanStatus streaming) {
        this.streaming = streaming;
    }

    public static String getPeerUrl(Peer peer) {
        return StringUtils
                .join("http://", peer.getIp(), ":", peer.getPort());
    }

    @Override
    public String toString() {
        return "Peer{" +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", online=" + online +
                ", streaming=" + streaming +
                '}';
    }
}
