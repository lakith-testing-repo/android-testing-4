package com.lakithrathnayake.myapplication04;

import android.app.Application;

import com.lakithrathnayake.myapplication04.greendao.db.DaoMaster;
import com.lakithrathnayake.myapplication04.greendao.db.DaoSession;

import org.greenrobot.greendao.database.Database;

public class MyApp extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseHelper helper = new DatabaseHelper(this);
        Database db = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
