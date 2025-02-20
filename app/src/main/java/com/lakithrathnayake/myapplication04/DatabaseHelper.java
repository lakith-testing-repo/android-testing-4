package com.lakithrathnayake.myapplication04;

import android.content.Context;

import com.lakithrathnayake.myapplication04.greendao.db.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class DatabaseHelper extends DaoMaster.OpenHelper {
    public DatabaseHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
