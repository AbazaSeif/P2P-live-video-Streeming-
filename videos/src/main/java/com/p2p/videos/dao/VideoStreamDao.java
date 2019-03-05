package com.p2p.videos.dao;

import com.p2p.dao.AbstractDao;
import com.p2p.files.models.UploadedFile;
import com.p2p.model.BooleanStatus;
import com.p2p.utils.DateTimeUtils;
import com.p2p.videos.model.VideoStream;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@Repository("videoStreamDao")
public class VideoStreamDao extends AbstractDao {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private DateTimeUtils dateTimeUtils;

    @PostConstruct
    private void initializeDao() {
        setSessionFactory(sessionFactory);
        setDateTimeUtils(dateTimeUtils);
    }

    @SuppressWarnings("unchecked")
    public List<VideoStream> getVideoStreams(UploadedFile uploadedFile, BooleanStatus booleanStatus,
                                             String... fetchClasses) {
        Session session = getCurrentSession();
        Criteria completeCriteria = session.createCriteria(VideoStream.class);
        if (uploadedFile != null) {
            completeCriteria.add(Restrictions.eq("uploadedFile", uploadedFile));
        }
        if (booleanStatus != null) {
            completeCriteria.add(Restrictions.eq("status", booleanStatus));
        }
        completeCriteria.addOrder(Order.desc("createdTime"));
        completeCriteria = setFetchClasses(completeCriteria, FetchMode.JOIN, fetchClasses);

        return completeCriteria.list();
    }

    public VideoStream lazyLoadFields(VideoStream videoStream) {
        Hibernate.initialize(videoStream.getUploadedFile());
        return videoStream;
    }
}
