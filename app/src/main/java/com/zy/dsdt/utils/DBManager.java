package com.zy.dsdt.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zy.dsdt.bean.BlankQuestionPDao;
import com.zy.dsdt.bean.ChoiceQuestionDao;
import com.zy.dsdt.bean.DaoMaster;
import com.zy.dsdt.bean.DaoSession;
import com.zy.dsdt.bean.UserClassDao;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.bean.WrongQuestionDao;

/**
 * Created by Chen on 2016/5/13.
 */
public class DBManager {
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    public DBManager(Context context) {
        initDatabase(context);
    }

    public void initDatabase(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "dsdt.db", null);

        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public UserClassDao getUserClassDao() {
        return daoSession.getUserClassDao();
    }

    public UserDao getUserDao() {
        return daoSession.getUserDao();
    }

    public ChoiceQuestionDao getChoiceQuestionDao() {
        return daoSession.getChoiceQuestionDao();
    }

    public BlankQuestionPDao getBlankQuestionPDao() {
        return daoSession.getBlankQuestionPDao();
    }

    public WrongQuestionDao getWrongQuestionDao() {
        return daoSession.getWrongQuestionDao();
    }
}
