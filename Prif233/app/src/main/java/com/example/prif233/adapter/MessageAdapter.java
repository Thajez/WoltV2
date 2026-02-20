package com.example.prif233.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prif233.R;
import com.example.prif233.dto.chat.MessageResponseDTO;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<MessageResponseDTO> {
    private final int currentUserId;

    public MessageAdapter(@NonNull Context context, @NonNull List<MessageResponseDTO> messages, int currentUserId) {
        super(context, 0, messages);
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);
        }

        MessageResponseDTO message = getItem(position);

        TextView senderName = view.findViewById(R.id.messageSenderName);
        TextView messageText = view.findViewById(R.id.messageText);
        TextView messageTime = view.findViewById(R.id.messageTime);
        LinearLayout messageContainer = view.findViewById(R.id.messageContainer);

        if (message != null) {
            senderName.setText(message.getSenderName());
            messageText.setText(message.getText());

            String time = message.getSentAt();
            if (time.contains("T")) {
                time = time.split("T")[1].substring(0, 5);
            }
            messageTime.setText(time);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageContainer.getLayoutParams();
            if (message.getSenderId() == currentUserId) {
                params.gravity = Gravity.END;
                messageContainer.setBackgroundResource(R.drawable.message_bubble_sent);
            } else {
                params.gravity = Gravity.START;
                messageContainer.setBackgroundResource(R.drawable.message_bubble_received);
            }
            messageContainer.setLayoutParams(params);
        }

        return view;
    }
}