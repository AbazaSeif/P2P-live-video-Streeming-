package com.p2p.files.dao;

import com.p2p.dao.AbstractDao;
import com.p2p.files.models.UploadedFile;
import com.p2p.files.models.FileChunk;
import com.p2p.utils.DateTimeUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@Repository("uploadedFileChunkDao")
public class UploadedFileChunkDao extends AbstractDao {

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
    public List<FileChunk> getFileChunksByFile(UploadedFile uploadedFile, String... fetchClasses) {
        Session session = getCurrentSession();
        Criteria completeCriteria = session.createCriteria(FileChunk.class);
        completeCriteria.add(Restrictions.eq("uploadedFile", uploadedFile));
        completeCriteria = setFetchClasses(completeCriteria, FetchMode.JOIN, fetchClasses);
        return completeCriteria.list();
    }

    public FileChunk getFileChunkByHash(String hash, String... fetchClasses) {
        Session session = getCurrentSession();
        Criteria completeCriteria = session.createCriteria(FileChunk.class);
        completeCriteria.add(Restrictions.eq("chunkHash", hash));
        completeCriteria = setFetchClasses(completeCriteria, FetchMode.JOIN, fetchClasses);
        return (FileChunk) completeCriteria.uniqueResult();
    }
}
