package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.UPDATE_PASSWORD_URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.RestOperations;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PasswordChange extends AppCompatActivity {
    private Integer currentId;
    private EditText oldPasswordField;
    private EditText newPasswordField;
    private EditText newPasswordField2;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_change);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentId = getIntent().getIntExtra("userId", -1);

        oldPasswordField = findViewById(R.id.oldPasswordField);
        newPasswordField = findViewById(R.id.newPasswordField);
        newPasswordField2 = findViewById(R.id.newPasswordField2);
    }

    public void updatePassword(View view) {
        String oldPassword = oldPasswordField.getText().toString().trim();
        String newPassword1 = newPasswordField.getText().toString().trim();
        String newPassword2 = newPasswordField2.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword1.isEmpty() || newPassword2.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword1.equals(newPassword2)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("oldPassword", oldPassword);
        jsonBody.addProperty("newPassword1", newPassword1);
        jsonBody.addProperty("newPassword2", newPassword2);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url = UPDATE_PASSWORD_URL + currentId;
                String response = RestOperations.sendPut(url, gson.toJson(jsonBody));

                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                handler.post(() ->
                        Toast.makeText(this, "Error updating password: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    public void cancel(View view) {
        finish();
    }
}