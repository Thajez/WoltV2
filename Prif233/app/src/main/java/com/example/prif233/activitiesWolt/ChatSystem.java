package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.GET_OR_CREATE_CHAT;
import static com.example.prif233.Utils.Constants.SEND_MESSAGE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prif233.R;
import com.example.prif233.Utils.LocalDateAdapter;
import com.example.prif233.Utils.LocalDateTimeAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.adapter.MessageAdapter;
import com.example.prif233.dto.chat.ChatResponseDTO;
import com.example.prif233.dto.chat.MessageResponseDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatSystem extends AppCompatActivity {

    private int orderId;
    private int userId;
    private int chatId;
    private Handler refreshHandler;
    private Runnable refreshRunnable;
    private static final int REFRESH_INTERVAL = 5000;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_system);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId", 0);
        userId = intent.getIntExtra("userId", 0);

        System.out.println("ChatSystem - orderId: " + orderId + ", userId: " + userId);

        if (orderId == 0 || userId == 0) {
            Toast.makeText(this, "Error: Invalid order or user ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupAutoRefresh();
        getOrCreateChat();
    }

    private void setupAutoRefresh() {
        refreshHandler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                refreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
    }

    private void getOrCreateChat() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url = GET_OR_CREATE_CHAT.replaceAll("/$", "") + "/" + orderId;
                System.out.println("Getting/creating chat with URL: " + url);

                String response = RestOperations.sendPost(url, "");
                System.out.println("Chat response: " + response);

                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            ChatResponseDTO chat = gson.fromJson(response, ChatResponseDTO.class);
                            chatId = chat.getId();

                            TextView chatTitle = findViewById(R.id.chatTitle);
                            chatTitle.setText(chat.getName());

                            loadMessages();
                            startAutoRefresh();
                        } else {
                            Toast.makeText(this, "Failed to load chat", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing chat: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (IOException e) {
                System.out.println("Network error: " + e.getMessage());
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void loadMessages() {
        if (chatId == 0) return;

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url = GET_OR_CREATE_CHAT + orderId;

                String response = RestOperations.sendGet(url);

                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            ChatResponseDTO chat = gson.fromJson(response, ChatResponseDTO.class);

                            List<MessageResponseDTO> messages = chat.getMessages();
                            if (messages == null) {
                                messages = new ArrayList<>();
                            }

                            ListView messagesListElement = findViewById(R.id.messageList);
                            MessageAdapter adapter = new MessageAdapter(this, messages, userId);
                            messagesListElement.setAdapter(adapter);

                            if (!messages.isEmpty()) {
                                messagesListElement.setSelection(messages.size() - 1);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing messages: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.out.println("Network error loading messages: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void sendMessage(View view) {
        EditText messageBody = findViewById(R.id.bodyField);
        String messageText = messageBody.getText().toString().trim();

        if (messageText.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("senderId", userId);
        jsonObject.addProperty("text", messageText);

        String message = gson.toJson(jsonObject);

        executor.execute(() -> {
            try {
                String url = SEND_MESSAGE + chatId;

                String response = RestOperations.sendPost(url, message);

                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            messageBody.setText("");
                            loadMessages();
                        } else {
                            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void startAutoRefresh() {
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private void stopAutoRefresh() {
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chatId != 0) {
            startAutoRefresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoRefresh();
    }
}