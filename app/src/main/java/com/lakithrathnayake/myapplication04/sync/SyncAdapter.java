package com.lakithrathnayake.myapplication04.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.lakithrathnayake.myapplication04.DatabaseConnection;
import com.lakithrathnayake.myapplication04.MainActivity;
import com.lakithrathnayake.myapplication04.MyApp;
import com.lakithrathnayake.myapplication04.greendao.db.DaoSession;
import com.lakithrathnayake.myapplication04.greendao.db.Items;
import com.lakithrathnayake.myapplication04.greendao.db.ItemsDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private Context mContext;
    private Connection connection;
    SQLiteDatabase database;
    private DaoSession daoSession;
    private ItemsDao itemsDao;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        daoSession = ((MyApp)getContext()).getDaoSession();
        itemsDao = daoSession.getItemsDao();

        try {
            connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM items";
            ResultSet resultSet = statement.executeQuery(sql);

            List<Items> localItems = new ArrayList<>();
            Map<Integer, Items> localItemsMap = new HashMap<>();
            for (Items item :
                    localItems) {
                localItemsMap.put(item.getItem_id(), item);
            }

            List<Items> itemsToSync = new ArrayList<>();

            while (resultSet.next()) {
                Integer itemId = Integer.valueOf(resultSet.getString("itm_id"));
                String itemCode = resultSet.getString("itm_code");
                String itemDesc = resultSet.getString("itm_desc");

                Items existingItem = localItemsMap.get(itemId);
                if(existingItem != null) {
                    existingItem.setItem_code(itemCode);
                    existingItem.setItem_desc(itemDesc);
                    itemsToSync.add(existingItem);
                    localItemsMap.remove(itemId);
                } else {
                    // Create new item if it doesn't exist
                    Items newItem = new Items();
                    newItem.setItem_id(itemId);
                    newItem.setItem_code(itemCode);
                    newItem.setItem_desc(itemDesc);
                    itemsToSync.add(newItem);
                }
            }
            itemsDao.insertOrReplaceInTx(itemsToSync);
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Clean up database connection
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
