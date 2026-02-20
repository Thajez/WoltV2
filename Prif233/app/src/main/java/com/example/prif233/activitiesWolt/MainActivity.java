package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.VALIDATE_USER_URL;

import android.content.Intent;
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
import com.example.prif233.dto.ErrorResponseDTO;
import com.example.prif233.dto.user.LoginRequestDTO;
import com.example.prif233.dto.user.LoginResponseDTO;
import com.example.prif233.model.UserType;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Gson gson;

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

        gson = new Gson();
    }

    public void validateUser(View view) {
        EditText login = findViewById(R.id.loginField);
        EditText password = findViewById(R.id.passwordField);

        String loginText = login.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (loginText.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordText.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequestDTO requestDTO = new LoginRequestDTO(loginText, passwordText);
        String requestJson = gson.toJson(requestDTO);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(VALIDATE_USER_URL, requestJson);

                handler.post(() -> {
                    if (response.isEmpty() || response.equals("Error")) {
                        Toast.makeText(MainActivity.this, "Connection error. Please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        if (response.contains("\"message\"")) {
                            ErrorResponseDTO errorResponse = gson.fromJson(response, ErrorResponseDTO.class);
                            Toast.makeText(MainActivity.this, errorResponse.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            LoginResponseDTO loginResponse = gson.fromJson(response, LoginResponseDTO.class);
                            Toast.makeText(MainActivity.this, "Welcome, " + loginResponse.getName() + "!", Toast.LENGTH_SHORT).show();

                            Intent intent;
                            if("DRIVER".equals(loginResponse.getUserType())){
                                intent = new Intent(MainActivity.this, MyOrders.class);
                                intent.putExtra("userId", loginResponse.getId());
                                intent.putExtra("isDriver", true);
                                intent.putExtra("isDeliveries", true);
                                intent.putExtra("history", false);
                            } else if ("BASIC".equals(loginResponse.getUserType())) {
                                intent = new Intent(MainActivity.this, WoltRestaurants.class);
                                intent.putExtra("userId", loginResponse.getId());
                                intent.putExtra("isDriver", false);
                            } else {
                                Toast.makeText(MainActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error processing response " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(MainActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    public void loadRegWindow(View view) {
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }
}