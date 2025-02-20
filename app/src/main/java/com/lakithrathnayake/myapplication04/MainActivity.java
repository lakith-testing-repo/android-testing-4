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
import com.lakithrathnayake.myapplication04.greendao.db.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DaoSession daoSession;
    Button btnSave, btnView;
    EditText editTextName, editTextEmail, editTextPhone;

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
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);

        daoSession = ((MyApp) getApplication()).getDaoSession();

        btnSave.setOnClickListener(v -> {
            User newUser = new User(
                    null,
                    editTextName.getText().toString().trim(),
                    editTextEmail.getText().toString().trim(),
                    editTextPhone.getText().toString().trim());
            try {
                daoSession.getUserDao().insert(newUser);
                Toast.makeText(this, "Successfully saved", Toast.LENGTH_LONG).show();
                editTextName.getText().clear();
                editTextEmail.getText().clear();
                editTextPhone.getText().clear();
                editTextName.requestFocus();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        btnView.setOnClickListener(v -> {
            List<User> users = daoSession.getUserDao().loadAll();
            if(!users.isEmpty()) {
                for (User u :
                        users) {
                    Toast.makeText(this, "Names: " + u.getName(), Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(this, "No data to show", Toast.LENGTH_LONG).show();
            }

        });
    }
}