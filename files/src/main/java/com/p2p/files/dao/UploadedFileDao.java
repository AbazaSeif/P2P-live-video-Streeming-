package com.p2p.files.dao;

import com.p2p.dao.AbstractDao;
import com.p2p.files.models.UploadedFile;
import com.p2p.model.BooleanStatus;
import com.p2p.utils.DateTimeUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@Repository("uploadedFileDao")
public class UploadedFileDao extends AbstractDao {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private DateTimeUtils dateTimeUtils;

    @PostConstruct
    private void initializeDao() {
        setSessionFactory(sessionFactory);
        setDateTimeUtils(dateTimeUtils);
    }

    public UploadedFile getFileByHash(String hash) {
        Session session = getCurrentSession();
        Criteria completeCriteria = session.createCriteria(UploadedFile.class);
        completeCriteria.add(Restrictions.eq("fileHash", hash));
        return (UploadedFile) completeCriteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<UploadedFile> getFilesByHash(List<String> fileHashes) {
        Session session = getCurrentSession();
        Criteria completeCriteria = session.createCriteria(UploadedFile.class);
        completeCriteria.add(Restrictions.in("fileHash", fileHashes));
        return completeCriteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<UploadedFile> getAllFiles(BooleanStatus status) {
        Session session = getCurrentSession();
        Criteria completeCriteria = session.createCriteria(UploadedFile.class);
        if (status != null) {
            completeCriteria.add(Restrictions.eq("status", status));
        }
        return completeCriteria.list();
    }
}
