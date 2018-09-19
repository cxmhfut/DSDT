package com.zy.dsdt.bean;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig userClassDaoConfig;
    private final DaoConfig choiceQuestionDaoConfig;
    private final DaoConfig blankQuestionPDaoConfig;
    private final DaoConfig wrongQuestionDaoConfig;

    private final UserDao userDao;
    private final UserClassDao userClassDao;
    private final ChoiceQuestionDao choiceQuestionDao;
    private final BlankQuestionPDao blankQuestionPDao;
    private final WrongQuestionDao wrongQuestionDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        userClassDaoConfig = daoConfigMap.get(UserClassDao.class).clone();
        userClassDaoConfig.initIdentityScope(type);

        choiceQuestionDaoConfig = daoConfigMap.get(ChoiceQuestionDao.class).clone();
        choiceQuestionDaoConfig.initIdentityScope(type);

        blankQuestionPDaoConfig = daoConfigMap.get(BlankQuestionPDao.class).clone();
        blankQuestionPDaoConfig.initIdentityScope(type);

        wrongQuestionDaoConfig = daoConfigMap.get(WrongQuestionDao.class).clone();
        wrongQuestionDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        userClassDao = new UserClassDao(userClassDaoConfig, this);
        choiceQuestionDao = new ChoiceQuestionDao(choiceQuestionDaoConfig, this);
        blankQuestionPDao = new BlankQuestionPDao(blankQuestionPDaoConfig, this);
        wrongQuestionDao = new WrongQuestionDao(wrongQuestionDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(UserClass.class, userClassDao);
        registerDao(ChoiceQuestion.class, choiceQuestionDao);
        registerDao(BlankQuestionP.class, blankQuestionPDao);
        registerDao(WrongQuestion.class, wrongQuestionDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        userClassDaoConfig.getIdentityScope().clear();
        choiceQuestionDaoConfig.getIdentityScope().clear();
        blankQuestionPDaoConfig.getIdentityScope().clear();
        wrongQuestionDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public UserClassDao getUserClassDao() {
        return userClassDao;
    }

    public ChoiceQuestionDao getChoiceQuestionDao() {
        return choiceQuestionDao;
    }

    public BlankQuestionPDao getBlankQuestionPDao() {
        return blankQuestionPDao;
    }

    public WrongQuestionDao getWrongQuestionDao() {
        return wrongQuestionDao;
    }

}
