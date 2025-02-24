package com.lakithrathnayake.myapplication04;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lakithrathnayake.myapplication04.greendao.db.DaoSession;
import com.lakithrathnayake.myapplication04.greendao.db.Items;
import com.lakithrathnayake.myapplication04.greendao.db.ItemsDao;
import com.lakithrathnayake.myapplication04.greendao.db.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DaoSession daoSession;
    Button btnSave, btnView, btnDownload;
    EditText editTextName, editTextEmail, editTextPhone, editTextCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSave = findViewById(R.id.btnSave);
        btnView = findViewById(R.id.btnView);
        btnDownload = findViewById(R.id.btnDownload);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextCity = findViewById(R.id.editTextCity);

        daoSession = ((MyApp) getApplication()).getDaoSession();

        btnSave.setOnClickListener(v -> {
            User newUser = new User(
                    null,
                    editTextName.getText().toString().trim(),
                    editTextEmail.getText().toString().trim(),
                    editTextPhone.getText().toString().trim(),
                    editTextCity.getText().toString().trim()
            );
            try {
                daoSession.getUserDao().insert(newUser);
                Toast.makeText(this, "Successfully saved", Toast.LENGTH_LONG).show();
                editTextName.getText().clear();
                editTextEmail.getText().clear();
                editTextPhone.getText().clear();
                editTextCity.getText().clear();
                editTextName.requestFocus();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        btnView.setOnClickListener(v -> {
//            List<User> users = daoSession.getUserDao().loadAll();
//            if(!users.isEmpty()) {
//                for (User u :
//                        users) {
//                    Toast.makeText(this, "Names: " + u.getCity(), Toast.LENGTH_LONG).show();
//                }
//            } else {
//
//                Toast.makeText(this, "No data to show", Toast.LENGTH_LONG).show();
//            }

            Connection connection = null;
            try {
                connection = DatabaseConnection.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (connection != null) {
                Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Not connected", Toast.LENGTH_LONG).show();
            }
        });

        btnDownload.setOnClickListener(v -> {
            DownloadItems();
        });
    }

    private void DownloadItems() {
        new Thread(() -> {
            Connection connection = null;
            try {
                connection = DatabaseConnection.getConnection();
                if(connection != null) {
                    Statement statement = connection.createStatement();
                    String sql = "SELECT * FROM items";
                    ResultSet resultSet = statement.executeQuery(sql);

                    List<Items> downloadedItems = new ArrayList<>();

                    while (resultSet.next()) {
                        Items item = new Items();
                        item.setItem_id(Integer.valueOf(resultSet.getString("itm_id")));
                        item.setItem_code(resultSet.getString("itm_code"));
                        item.setItem_desc(resultSet.getString("itm_desc"));

                        downloadedItems.add(item);
                    }
                    resultSet.close();
                    statement.close();

                    try {
                        daoSession.getItemsDao().insertOrReplaceInTx(downloadedItems);
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "Successfully downloaded and saved " + downloadedItems.size() + " items",
                                    Toast.LENGTH_LONG).show();
                        });
                    } catch (Exception e) {
                        final String errorMessage = e.getMessage();
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "Error saving downloaded items: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Could not connect to database", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (SQLException e) {
                final String errorMessage = e.getMessage();
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Download error: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            } finally {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}