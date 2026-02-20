package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.GET_ALL_RESTAURANTS_URL;
import static com.example.prif233.Utils.Constants.GET_USER_BY_ID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.LocalDateTimeAdapter;
import com.example.prif233.Utils.LocalTimeAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.adapter.RestaurantAdapter;
import com.example.prif233.model.BasicUser;
import com.example.prif233.model.Restaurant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WoltRestaurants extends AppCompatActivity {
    private BasicUser currentUser;
    private int userId;
    private boolean isDriver;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wolt_restaurants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        isDriver = intent.getBooleanExtra("isDriver", false);

        loadUser();
    }

    private void loadUser() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_USER_BY_ID + userId);
                handler.post(() -> {
                    if (response.isEmpty() || response.equals("Error")) {
                        Toast.makeText(this, "Failed to load user", Toast.LENGTH_LONG).show();
                    } else {
                        currentUser = gson.fromJson(response, BasicUser.class);
                        loadRestaurants();
                    }
                });
        } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadRestaurants() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_ALL_RESTAURANTS_URL);
                handler.post(() -> {
                    if (response.isEmpty() || response.equals("Error")) {
                        Toast.makeText(this, "Failed to load restaurants", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        Type restaurantListType = new TypeToken<List<Restaurant>>() {}.getType();
                        List<Restaurant> restaurantListFromJson = gson.fromJson(response, restaurantListType);

                        ListView restaurantListElement = findViewById(R.id.restaurantList);
                        RestaurantAdapter adapter = new RestaurantAdapter(this, restaurantListFromJson);
                        restaurantListElement.setAdapter(adapter);

                        restaurantListElement.setOnItemClickListener((parent, view, position, id) -> {
                            Restaurant selectedRestaurant = restaurantListFromJson.get(position);
                            Intent intentMenu = new Intent(WoltRestaurants.this, MenuActivity.class);
                            intentMenu.putExtra("restaurantId", selectedRestaurant.getId());
                            intentMenu.putExtra("userId", currentUser.getId());
                            startActivity(intentMenu);
                        });
                    } catch (Exception e) {
                        Toast.makeText(this, "Error loading restaurants" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void viewPurchaseHistory(View view) {
        Intent intent = new Intent(WoltRestaurants.this, MyOrders.class);
        intent.putExtra("userId", currentUser.getId());
        intent.putExtra("userType", currentUser.getUserType());
        intent.putExtra("history", true);
        intent.putExtra("isDriver", isDriver);
        intent.putExtra("isDeliveries", false);
        startActivity(intent);
    }

    public void viewMyAccount(View view) {
        Intent intent = new Intent(WoltRestaurants.this, DetailsMenu.class);
        intent.putExtra("userId", currentUser.getId());
        intent.putExtra("isDriver", isDriver);
        startActivity(intent);
    }

    public void logout(View view) {
        currentUser = null;
        userId = -1;
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WoltRestaurants.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void viewOrders(View view) {
        Intent intent = new Intent(WoltRestaurants.this, MyOrders.class);
        intent.putExtra("userId", currentUser.getId());
        intent.putExtra("userType", currentUser.getUserType());
        intent.putExtra("history", false);
        intent.putExtra("isDriver", isDriver);
        intent.putExtra("isDeliveries", false);
        startActivity(intent);
    }
}