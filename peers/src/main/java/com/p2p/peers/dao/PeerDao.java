package com.p2p.peers.dao;

import java.util.List;
import javax.annotation.PostConstruct;
import com.p2p.model.BooleanStatus;
import com.p2p.utils.DateTimeUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.p2p.dao.AbstractDao;
import com.p2p.peers.model.Peer;

@Repository("peerDao")
public class PeerDao extends AbstractDao {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private DateTimeUtils dateTimeUtils;
    @PostConstruct
    private void initializeDao() {
        setSessionFactory(sessionFactory);
        setDateTimeUtils(dateTimeUtils);
    }

    /**
     * Gets all peers.
     */
    @SuppressWarnings("unchecked")
    public List<Peer> getAllPeers(BooleanStatus onlineStatus, BooleanStatus streamingStatus) {

        Session session = getCurrentSession();
        Criteria completeCriteria = session.createCriteria(Peer.class);
        if (onlineStatus != null) {
            completeCriteria.add(Restrictions.eq("online", onlineStatus));
        }
        if (streamingStatus != null) {
            completeCriteria.add(Restrictions.eq("streaming", streamingStatus));
        }
        return completeCriteria.list();
    }

    /**
     * Gets peer by ip and port.
     */
    public Peer getPeerByIpAndPort(String ip, String port) {
        Session session = getCurrentSession();
        Criteria completeCriteria = session.createCriteria(Peer.class);
        completeCriteria.add(Restrictions.eq("ip", ip));
        completeCriteria.add(Restrictions.eq("port", port));
        return (Peer) completeCriteria.uniqueResult();
    }
}