package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.ASSIGN_DELIVERY_URL;
import static com.example.prif233.Utils.Constants.COMPLETE_DELIVERY_URL;
import static com.example.prif233.Utils.Constants.GET_AVAILABLE_ORDERS;
import static com.example.prif233.Utils.Constants.GET_CLIENT_ACTIVE_ORDERS;
import static com.example.prif233.Utils.Constants.GET_CLIENT_ORDER_HISTORY;
import static com.example.prif233.Utils.Constants.GET_DRIVER_ACTIVE_DELIVERIES;
import static com.example.prif233.Utils.Constants.GET_DRIVER_DELIVERY_HISTORY;
import static com.example.prif233.Utils.Constants.START_DELIVERY_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.LocalDateAdapter;
import com.example.prif233.Utils.LocalDateTimeAdapter;
import com.example.prif233.Utils.LocalTimeAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.adapter.MyOrdersAdapter;
import com.example.prif233.dto.restaurant.OrderResponseDTO;
import com.example.prif233.model.FoodOrder;
import com.example.prif233.model.OrderStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyOrders extends AppCompatActivity {
    private int userId;
    private boolean isDriver;
    private boolean isHistory;
    private boolean isDeliveries;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        isDriver = intent.getBooleanExtra("isDriver", false);
        isHistory = intent.getBooleanExtra("history", false);
        isDeliveries = intent.getBooleanExtra("isDeliveries", false);

        hideButtons(isDeliveries);
        loadOrders();
    }

    private void loadOrders() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url;

                if (isDeliveries) {
                    url = GET_DRIVER_ACTIVE_DELIVERIES + userId;
                } else if (isDriver) {
                    if (isHistory) {
                        url = GET_DRIVER_DELIVERY_HISTORY + userId;
                    } else {
                        url = GET_AVAILABLE_ORDERS;
                    }
                } else {
                    if (isHistory) {
                        url = GET_CLIENT_ORDER_HISTORY + userId;
                    } else {
                        url = GET_CLIENT_ACTIVE_ORDERS + userId;
                    }
                }
                String response = RestOperations.sendGet(url);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error")) {
                            Type ordersListType = new TypeToken<List<OrderResponseDTO>>() {}.getType();
                            List<OrderResponseDTO> ordersListFromJson = gson.fromJson(response, ordersListType);

                            if (ordersListFromJson != null && !ordersListFromJson.isEmpty()) {
                                ListView ordersListElement = findViewById(R.id.myOrderList);
                                MyOrdersAdapter adapter = new MyOrdersAdapter(this, ordersListFromJson, userId, isDriver);
                                ordersListElement.setAdapter(adapter);
                                ordersListElement.setOnItemClickListener((parent, view, position, id) -> {
                                    OrderResponseDTO selectedOrder = ordersListFromJson.get(position);

                                    if (isDriver && !isDeliveries && !isHistory) {
                                        assignOrderToDriver(selectedOrder.getId());
                                    } else if (isDeliveries) {
                                        showDriverDeliveryDialog(selectedOrder);
                                    } else if (isHistory) {
                                        showHistoryDialog(selectedOrder.getId());
                                    } else {
                                        Intent intentChat = new Intent(MyOrders.this, ChatSystem.class);
                                        intentChat.putExtra("orderId", selectedOrder.getId());
                                        intentChat.putExtra("userId", userId);
                                        startActivity(intentChat);
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(this, "No orders available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error loading orders " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void assignOrderToDriver(int orderId) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPut(ASSIGN_DELIVERY_URL + userId + "/" + orderId, "");

                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Toast.makeText(this, "Order assigned successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MyOrders.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("isDriver", true);
                        intent.putExtra("isDeliveries", true);
                        intent.putExtra("history", false);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to assign order", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        });
    }

    private void showDriverDeliveryDialog(OrderResponseDTO order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order #" + order.getId());

        String progressActionText;
        if ("READY".equals(order.getOrderStatus())) {
            progressActionText = "Order Picked Up";
        } else if ("IN_DELIVERY".equals(order.getOrderStatus())) {
            progressActionText = "Order Delivered";
        } else {
            progressActionText = null;
        }

        String[] options;
        if (progressActionText != null) {
            options = new String[]{"View Chat", progressActionText};
        } else {
            options = new String[]{"View Chat"};
        }

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intentChat = new Intent(MyOrders.this, ChatSystem.class);
                intentChat.putExtra("orderId", order.getId());
                intentChat.putExtra("userId", userId);
                startActivity(intentChat);
            } else if (which == 1) {
                if ("READY".equals(order.getOrderStatus())) {
                    startDelivery(order.getId());
                } else if ("IN_DELIVERY".equals(order.getOrderStatus())) {
                    completeDelivery(order.getId());
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void startDelivery(int orderId) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url = START_DELIVERY_URL + userId + "/" + orderId;

                String response = RestOperations.sendPut(url, "");

                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Toast.makeText(this, "Order picked up! Delivery started.", Toast.LENGTH_SHORT).show();
                        loadOrders();
                    } else {
                        Toast.makeText(this, "Failed to start delivery", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void completeDelivery(int orderId) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url = COMPLETE_DELIVERY_URL + userId + "/" + orderId;

                String response = RestOperations.sendPut(url, "");

                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Toast.makeText(this, "Order delivered successfully!", Toast.LENGTH_SHORT).show();
                        loadOrders();
                    } else {
                        Toast.makeText(this, "Failed to complete delivery", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showHistoryDialog(int orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Options");

        String[] options = {"View Chat", "Leave a Review"};

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent intentChat = new Intent(MyOrders.this, ChatSystem.class);
                intentChat.putExtra("orderId", orderId);
                intentChat.putExtra("userId", userId);
                startActivity(intentChat);
            } else {
                Intent intentReview = new Intent(MyOrders.this, ReviewActivity.class);
                intentReview.putExtra("orderId", orderId);
                intentReview.putExtra("userId", userId);
                startActivity(intentReview);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void hideButtons(boolean isDeliveries) {
        Button viewHistoryButton = findViewById(R.id.historyButton2);
        Button viewAvailableButton = findViewById(R.id.availableOrdersButton);
        Button editDetailsButton = findViewById(R.id.editDetailsButton);
        Button logoutButton = findViewById(R.id.logoutButton2);
        if (isDeliveries) {
            viewHistoryButton.setVisibility(View.VISIBLE);
            viewAvailableButton.setVisibility(View.VISIBLE);
            editDetailsButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            viewHistoryButton.setVisibility(View.GONE);
            viewAvailableButton.setVisibility(View.GONE);
            editDetailsButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    public void logout(View view) {
        userId = -1;
        isDriver = false;
        isHistory = false;
        isDeliveries = false;
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void editAccount(View view) {
        Intent intent = new Intent(this, DetailsMenu.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isDriver", isDriver);
        startActivity(intent);
    }

    public void viewHistory(View view) {
        if (!isDriver) return;

        Intent intent = new Intent(this, MyOrders.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isDriver", true);
        intent.putExtra("isDeliveries", false);
        intent.putExtra("history", true);
        startActivity(intent);
    }

    public void viewAvailable(View view) {
        if (!isDriver) return;

        Intent intent = new Intent(this, MyOrders.class);
        intent.putExtra("userId", userId);
        intent.putExtra("isDriver", true);
        intent.putExtra("isDeliveries", false);
        intent.putExtra("history", false);
        startActivity(intent);
    }
}