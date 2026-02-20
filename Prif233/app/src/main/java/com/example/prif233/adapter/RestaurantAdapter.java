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
import com.example.prif233.model.Restaurant;

import java.util.List;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    public RestaurantAdapter(@NonNull Context context, @NonNull List<Restaurant> restaurants) {
        super(context, 0, restaurants);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_restaurant, parent, false);
        }

        Restaurant restaurant = getItem(position);

        if (restaurant != null) {
            TextView nameTextView = convertView.findViewById(R.id.restaurantName);
            TextView addressTextView = convertView.findViewById(R.id.restaurantAddress);
            TextView phoneTextView = convertView.findViewById(R.id.restaurantPhone);
            TextView workHoursTextView = convertView.findViewById(R.id.workHours);

            String restaurantName = restaurant.getName();
            if (restaurant.getSurname() != null && !restaurant.getSurname().isEmpty()) {
                restaurantName += " " + restaurant.getSurname();
            }
            nameTextView.setText(restaurantName);

            if (restaurant.getAddress() != null && !restaurant.getAddress().isEmpty()) {
                addressTextView.setText("📍 " + restaurant.getAddress());
            } else {
                addressTextView.setText("📍 Address not available");
            }

            if (restaurant.getPhoneNumber() != null && !restaurant.getPhoneNumber().isEmpty()) {
                phoneTextView.setText("📞 " + restaurant.getPhoneNumber());
            } else {
                phoneTextView.setText("📞 Phone not available");
            }

            if (restaurant.getOpeningTime() != null && restaurant.getClosingTime() != null) {
                workHoursTextView.setText("🕐 " + restaurant.getOpeningTime() + " — " + restaurant.getClosingTime());
            } else {
                workHoursTextView.setText("🕐 Opening hours not available");
            }
        }

        return convertView;
    }
}

