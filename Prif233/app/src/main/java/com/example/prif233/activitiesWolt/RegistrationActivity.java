    package com.example.prif233.activitiesWolt;

    import static com.example.prif233.Utils.Constants.CREATE_BASIC_USER_URL;
    import static com.example.prif233.Utils.Constants.CREATE_DRIVER_USER_URL;
    import static com.example.prif233.Utils.Constants.VALIDATE_USER_URL;

    import android.app.DatePickerDialog;
    import android.content.Intent;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    import android.util.Log;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.CheckBox;
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
    import com.example.prif233.Utils.RestOperations;
    import com.example.prif233.dto.user.ClientCreateRequestDTO;
    import com.example.prif233.dto.user.DriverCreateRequestDTO;
    import com.example.prif233.model.BasicUser;
    import com.example.prif233.model.Driver;
    import com.example.prif233.model.UserType;
    import com.example.prif233.model.VehicleType;
    import com.google.gson.Gson;

    import org.w3c.dom.Text;

    import java.io.IOException;
    import java.time.LocalDate;
    import java.util.Calendar;
    import java.util.concurrent.Executor;
    import java.util.concurrent.Executors;

    public class RegistrationActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_registration);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            Spinner vehicleTypeSpinner = findViewById(R.id.regVehicleType);

            ArrayAdapter<VehicleType> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    VehicleType.values()
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            vehicleTypeSpinner.setAdapter(adapter);

            TextView licenceLabel = findViewById(R.id.licenceLabel);
            EditText licenceField = findViewById(R.id.regLicenceField);
            TextView bDayLabel = findViewById(R.id.bDayLabel);
            EditText bDayField = findViewById(R.id.regBDayField);
            TextView vehicleTypeLabel = findViewById(R.id.vehicleTypeLabel);
            CheckBox isDriverCheckBox = findViewById(R.id.regIsDriver);

            licenceLabel.setVisibility(View.GONE);
            licenceField.setVisibility(View.GONE);
            bDayLabel.setVisibility(View.GONE);
            bDayField.setVisibility(View.GONE);
            vehicleTypeLabel.setVisibility(View.GONE);
            vehicleTypeSpinner.setVisibility(View.GONE);

            isDriverCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int visibility = isChecked ? View.VISIBLE : View.GONE;

                licenceLabel.setVisibility(visibility);
                licenceField.setVisibility(visibility);
                bDayLabel.setVisibility(visibility);
                bDayField.setVisibility(visibility);
                vehicleTypeLabel.setVisibility(visibility);
                vehicleTypeSpinner.setVisibility(visibility);
            });

            bDayField.setOnClickListener(v -> showDatePickerDialog(bDayField));
        }

        public void createAccount(View view) {
            TextView login = findViewById(R.id.regLoginField);
            TextView psw = findViewById(R.id.regPasswordField);
            TextView name = findViewById(R.id.regNameField);
            TextView surname = findViewById(R.id.regSurnameField);
            TextView phone = findViewById(R.id.regPhoneField);
            TextView address = findViewById(R.id.regAddressField);
            CheckBox isDriverCheckBox = findViewById(R.id.regIsDriver);

            if (login.getText().toString().isEmpty() ||
                    psw.getText().toString().isEmpty() ||
                    name.getText().toString().isEmpty() ||
                    surname.getText().toString().isEmpty() ||
                    phone.getText().toString().isEmpty() ||
                    address.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isDriver = isDriverCheckBox.isChecked();
            String userInfo;
            Gson gson = new Gson();

            if (isDriver) {
                TextView licence = findViewById(R.id.regLicenceField);
                TextView bDay = findViewById(R.id.regBDayField);
                Spinner vehicleType = findViewById(R.id.regVehicleType);

                if (bDay.getText().toString().trim().isEmpty() || licence.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please fill all driver fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                LocalDate birthDate = LocalDate.parse(bDay.getText().toString());
                VehicleType selectedVehicleType = (VehicleType) vehicleType.getSelectedItem();

                DriverCreateRequestDTO driverDTO = new DriverCreateRequestDTO(
                        login.getText().toString().trim(),
                        psw.getText().toString().trim(),
                        name.getText().toString().trim(),
                        surname.getText().toString().trim(),
                        phone.getText().toString().trim(),
                        address.getText().toString().trim(),
                        licence.getText().toString().trim(),
                        birthDate,
                        selectedVehicleType
                );
                userInfo = gson.toJson(driverDTO);
            } else {
                ClientCreateRequestDTO clientDTO = new ClientCreateRequestDTO(
                        login.getText().toString().trim(),
                        psw.getText().toString().trim(),
                        name.getText().toString().trim(),
                        surname.getText().toString().trim(),
                        phone.getText().toString().trim(),
                        address.getText().toString().trim()
                );
                userInfo = gson.toJson(clientDTO);
            }
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            String finalUserInfo = userInfo;
            executor.execute(() -> {
                try {
                    String url = isDriver ? CREATE_DRIVER_USER_URL : CREATE_BASIC_USER_URL;
                    String response = RestOperations.sendPost(url, finalUserInfo);

                    handler.post(() -> {
                        if (!response.isEmpty() && !response.equals("Error")) {
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (IOException e) {
                    handler.post(() ->
                            Toast.makeText(RegistrationActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
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