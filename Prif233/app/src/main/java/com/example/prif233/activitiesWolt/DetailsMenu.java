package com.example.prif233.activitiesWolt;

import static com.example.prif233.Utils.Constants.DELETE_USER_URL;
import static com.example.prif233.Utils.Constants.GET_USER_BY_ID;
import static com.example.prif233.Utils.Constants.UPDATE_BASIC_USER_URL;
import static com.example.prif233.Utils.Constants.UPDATE_DRIVER_USER_URL;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.prif233.Utils.LocalTimeAdapter;
import com.example.prif233.Utils.RestOperations;
import com.example.prif233.model.BasicUser;
import com.example.prif233.model.Driver;
import com.example.prif233.model.VehicleType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailsMenu extends AppCompatActivity {
    private boolean isDriver;
    private Integer currentId;
    private BasicUser UserForUpdate;

    private EditText loginField;
    private EditText nameField;
    private EditText surnameField;
    private EditText phoneField;
    private EditText addressField;
    private EditText licenceField;
    private EditText bDayField;
    private Spinner vehicleTypeSpinner;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginField = findViewById(R.id.updateLoginField);
        nameField = findViewById(R.id.updateNameField);
        surnameField = findViewById(R.id.updateSurnameField);
        phoneField = findViewById(R.id.updatePhoneField);
        addressField = findViewById(R.id.updateAddressField);
        licenceField = findViewById(R.id.updateLicenceField);
        bDayField = findViewById(R.id.updateBDayField);
        vehicleTypeSpinner = findViewById(R.id.updateVehicleType);

        bDayField.setOnClickListener(v -> showDatePickerDialog(bDayField));

        ArrayAdapter<VehicleType> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                VehicleType.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(adapter);

        TextView licenceLabel = findViewById(R.id.licenceLabel);
        TextView bDayLabel = findViewById(R.id.bDayLabel);
        TextView vehicleTypeLabel = findViewById(R.id.vehicleTypeLabel);

        licenceLabel.setVisibility(View.GONE);
        licenceField.setVisibility(View.GONE);
        bDayLabel.setVisibility(View.GONE);
        bDayField.setVisibility(View.GONE);
        vehicleTypeLabel.setVisibility(View.GONE);
        vehicleTypeSpinner.setVisibility(View.GONE);

        isDriver = getIntent().getBooleanExtra("isDriver", false);
        currentId = getIntent().getIntExtra("userId", -1);

        if (isDriver) {
            licenceLabel.setVisibility(View.VISIBLE);
            licenceField.setVisibility(View.VISIBLE);
            bDayLabel.setVisibility(View.VISIBLE);
            bDayField.setVisibility(View.VISIBLE);
            vehicleTypeLabel.setVisibility(View.VISIBLE);
            vehicleTypeSpinner.setVisibility(View.VISIBLE);
        }

        fillFields();
    }

    private void fillFields() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url = GET_USER_BY_ID + currentId;
                String response = RestOperations.sendGet(url);

                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        try {
                            if (isDriver) {
                                UserForUpdate = gson.fromJson(response, Driver.class);
                                Driver driver = (Driver) UserForUpdate;

                                licenceField.setText(driver.getLicence());
                                if (driver.getbDate() != null) {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    bDayField.setText(driver.getbDate().format(formatter));
                                }

                                if (driver.getVehicleType() != null) {
                                    int spinnerPosition = ((ArrayAdapter<VehicleType>) vehicleTypeSpinner.getAdapter())
                                            .getPosition(driver.getVehicleType());
                                    vehicleTypeSpinner.setSelection(spinnerPosition);
                                }
                            } else {
                                UserForUpdate = gson.fromJson(response, BasicUser.class);
                            }

                            loginField.setText(UserForUpdate.getLogin());
                            nameField.setText(UserForUpdate.getName());
                            surnameField.setText(UserForUpdate.getSurname());
                            phoneField.setText(UserForUpdate.getPhoneNumber());
                            addressField.setText(UserForUpdate.getAddress());

                            Toast.makeText(this, "Account loaded successfully!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Error parsing user data: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load account", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                handler.post(() ->
                        Toast.makeText(this, "Error loading account: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    public void updatePassword(View view) {
        Intent intent = new Intent(this, PasswordChange.class);
        intent.putExtra("userId", currentId);
        startActivity(intent);
    }

    public void deleteAccount(View view) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete this account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> performDelete())
                .setNegativeButton("Cancel", null)
                .show();
    }
    public void performDelete() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try{
                String url = DELETE_USER_URL + currentId;

                String response = RestOperations.sendDelete(url);

                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Toast.makeText(this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error deleting account" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateAccount(View view) {
        UserForUpdate.setLogin(loginField.getText().toString().trim());
        UserForUpdate.setName(nameField.getText().toString().trim());
        UserForUpdate.setSurname(surnameField.getText().toString().trim());
        UserForUpdate.setPhoneNumber(phoneField.getText().toString().trim());
        UserForUpdate.setAddress(addressField.getText().toString().trim());

        if (isDriver && UserForUpdate instanceof Driver) {
            Driver driver = (Driver) UserForUpdate;
            driver.setLicence(licenceField.getText().toString().trim());
            driver.setbDate(LocalDate.parse(bDayField.getText().toString().trim()));
            System.out.println(driver.getbDate());
            driver.setVehicleType((VehicleType) vehicleTypeSpinner.getSelectedItem());
        }
        String jsonBody = gson.toJson(UserForUpdate);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String url = isDriver ? UPDATE_DRIVER_USER_URL : UPDATE_BASIC_USER_URL;
                url = url + currentId;
                String response = RestOperations.sendPut(url, jsonBody);

                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Toast.makeText(this, "Account updated successfully!", Toast.LENGTH_SHORT).show();
                        fillFields();
                    } else {
                        Toast.makeText(this, "Failed to update account", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error updating account: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDatePickerDialog(EditText bDayField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    bDayField.setText(formattedDate);
                },
                year - 18,
                month,
                day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        calendar.add(Calendar.YEAR, -100);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }
}