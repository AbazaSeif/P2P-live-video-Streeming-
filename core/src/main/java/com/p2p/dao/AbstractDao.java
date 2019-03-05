package com.p2p.dao;

import java.io.Serializable;
import com.p2p.utils.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.p2p.model.AbstractDatabaseObject;

public class AbstractDao {

    private SessionFactory sessionFactory;
    private DateTimeUtils dateTimeUtils;

    /**
     * Gets the current session.
     */
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public <T extends AbstractDatabaseObject> T get(Class<T> entityType, Serializable id) {
        Session session = getCurrentSession();
        T entity = session.get(entityType, id);
        return entity;
    }

    public <T extends AbstractDatabaseObject> Serializable create(T entity) {
        Session session = getCurrentSession();
        entity.setCreatedTime(dateTimeUtils.getApplicationCurrentTime());
        entity.setLastUpdatedTime(dateTimeUtils.getApplicationCurrentTime());
        return session.save(entity);
    }

    public <T extends AbstractDatabaseObject> void update(T entity) {
        Session session = getCurrentSession();
        entity.setCreatedTime(dateTimeUtils.getApplicationCurrentTime());
        entity.setLastUpdatedTime(dateTimeUtils.getApplicationCurrentTime());
        session.update(entity);
    }

    public <T extends AbstractDatabaseObject> void saveOrUpdate(T entity) {
        Session session = getCurrentSession();
        entity.setCreatedTime(
                ObjectUtils.defaultIfNull(entity.getCreatedTime(), dateTimeUtils.getApplicationCurrentTime()));
        entity.setLastUpdatedTime(dateTimeUtils.getApplicationCurrentTime());
        session.saveOrUpdate(entity);
    }

    public <T extends AbstractDatabaseObject> void delete(T entity) {
        Session session = getCurrentSession();
        session.delete(entity);
    }

    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected DateTimeUtils getDateTimeUtils() {
        return dateTimeUtils;
    }

    protected void setDateTimeUtils(DateTimeUtils dateTimeUtils) {
        this.dateTimeUtils = dateTimeUtils;
    }

    protected Criteria setFetchClasses(Criteria criteria, FetchMode fetchMode, String... assosciations) {
        if (CollectionUtils.sizeIsEmpty(assosciations)) {
            return criteria;
        }
        for (String association : assosciations) {
            criteria.setFetchMode(association, fetchMode);
        }
        return criteria;
    }

}
