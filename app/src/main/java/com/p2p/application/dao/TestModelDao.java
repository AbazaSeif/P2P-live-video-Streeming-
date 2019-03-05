package com.p2p.application.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.p2p.application.model.TestModel;

@Repository("testModelDao")
public class TestModelDao {

    @Autowired
    private SessionFactory sessionFactory;

    public String createTestModel(TestModel testModel) {
        Session session = sessionFactory.getCurrentSession();
        return (String) session.save(testModel);
    }

    public TestModel get(String id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(TestModel.class, id);
    }
}
