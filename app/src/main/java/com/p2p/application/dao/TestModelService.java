package com.p2p.application.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.p2p.application.model.TestModel;

@Service("userRoutineEventService")
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Throwable.class)
public class TestModelService {

    @Autowired
    private TestModelDao testModelDao;

    public String createTestModel(TestModel model) {
        return testModelDao.createTestModel(model);
    }

    public TestModel getTestModel(String id) {
        return testModelDao.get(id);
    }
}
