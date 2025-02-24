package com.lakithrathnayake.myapplication04;

import android.content.Context;

import com.lakithrathnayake.myapplication04.greendao.db.DaoMaster;
import com.lakithrathnayake.myapplication04.greendao.db.ItemsDao;
import com.lakithrathnayake.myapplication04.greendao.db.UserDao;

import org.greenrobot.greendao.database.Database;

public class DatabaseHelper extends DaoMaster.OpenHelper {
    private static final String DATABASE_NAME = "test.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        if(oldVersion < newVersion) {
            switch (oldVersion) {
                case 1:
                    db.execSQL("ALTER TABLE " + UserDao.TABLENAME +
                            " ADD COLUMN "  + UserDao.Properties.City.columnName + " TEXT;");
                    break;
                case 2:
                    ItemsDao.createTable(db, true);
            }
        }
    }
}
