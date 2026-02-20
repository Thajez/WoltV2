package com.example.prif233.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prif233.R;
import com.example.prif233.dto.restaurant.OrderResponseDTO;
import com.example.prif233.model.FoodOrder;

import java.util.List;

public class MyOrdersAdapter extends ArrayAdapter<OrderResponseDTO> {
    private int loggedUserId;
    private final boolean isDriver;

    public MyOrdersAdapter(@NonNull Context context, @NonNull List<OrderResponseDTO> orders, int loggedUserId, boolean isDriver) {
        super(context, 0, orders);
        this.loggedUserId = loggedUserId;
        this.isDriver = isDriver;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_order, parent, false);
        }

        OrderResponseDTO order = getItem(position);

        TextView restaurantLabel = view.findViewById(R.id.orderRestaurant);
        TextView orderTitle = view.findViewById(R.id.orderTitle);
        TextView orderPrice = view.findViewById(R.id.orderPrice);
        TextView userField = view.findViewById(R.id.userField);
        TextView statusField = view.findViewById(R.id.statusField);

        if (order != null) {
            restaurantLabel.setText(order.getRestaurantName() + " " + order.getRestaurantSurname());
            orderTitle.setText("Order #" + order.getId());
            orderPrice.setText(order.getPrice() != 0 ? "€" + String.format("%.2f", order.getPrice()) : "Price: N/A");

            if(isDriver) {
                userField.setText("Client: " + order.getBuyerName());
            } else {
                if (order.getDriverId() != null  && order.getDriverName() != null && order.getDriverSurname() != null) {
                    userField.setText("Driver: " + order.getDriverName() + " " + order.getDriverSurname());
                } else {
                    userField.setText("Driver: Not assigned");
                }
            }

            if (order.getOrderStatus() != null) {
                statusField.setText("Status: " + order.getOrderStatus().toString().charAt(0) +
                        order.getOrderStatus().toString().toLowerCase().substring(1));
            } else {
                statusField.setText("Status: N/A");
            }
        }

        return view;
    }
}

