package com.lakithrathnayake.myapplication04;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lakithrathnayake.myapplication04.greendao.db.DaoMaster;
import com.lakithrathnayake.myapplication04.greendao.db.DaoSession;
import com.lakithrathnayake.myapplication04.greendao.db.Items;
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
    ProgressBar progressBar;
    private TableLayout tableLayout;

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
        progressBar = findViewById(R.id.progressBar);
        tableLayout = findViewById(R.id.tableLayout);

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

//
            new Thread(() -> {
                List<Items> items = daoSession.getItemsDao().loadAll();
                runOnUiThread(() -> {
                    if(!items.isEmpty()){
                        clearTable();
                        populateTable(items);
                    } else {
                        Toast.makeText(this, "No items found in database", Toast.LENGTH_LONG).show();
                    }
                });
            }).start();
        });

        btnDownload.setOnClickListener(v -> {
            DownloadItems();
        });
    }

    private void populateTable(List<Items> items) {
        for (Items item :
                items) {
            TableRow row = new TableRow(this);
            row.setPadding(8, 8, 8, 8);

            TextView idView = new TextView(this);
            idView.setGravity(Gravity.CENTER);
            idView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            idView.setText(String.valueOf(item.getItem_id()));
            row.addView(idView);

            // Add Code cell
            TextView codeView = new TextView(this);
            codeView.setGravity(Gravity.CENTER);
            codeView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            codeView.setText(item.getItem_code());
            row.addView(codeView);

            // Add Description cell
            TextView descView = new TextView(this);
            descView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
            descView.setText(item.getItem_desc());
            descView.setPadding(20,0,0,0);
            row.addView(descView);
            row.setBackgroundResource(R.drawable.light_row_border);

            tableLayout.addView(row);
        }
    }

    private void clearTable() {
        if(tableLayout.getChildCount() > 1) {
            tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        }
    }

    private void DownloadItems() {
        new Thread(() -> {
            showProgress(true);
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
                showProgress(false);
            }
        }).start();
    }

    private void showProgress(boolean show) {
        runOnUiThread(() -> {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            btnDownload.setEnabled(!show);
        });
    }
}