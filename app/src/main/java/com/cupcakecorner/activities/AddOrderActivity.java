package com.cupcakecorner.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cupcakecorner.R;
import com.cupcakecorner.database.DatabaseHelper;
import com.cupcakecorner.database.SessionManager;
import com.cupcakecorner.models.Category;
import com.cupcakecorner.models.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddOrderActivity extends AppCompatActivity {

    private EditText quantityEditText;
    private Spinner pickupDateSpinner;
    private TextView priceTextView;
    private Button placeOrderButton;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Category category;
    private int categoryPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        String categoryName = getIntent().getStringExtra("categoryName");

        TextView textView = findViewById(R.id.categoryNameTextView);
        textView.setText(categoryName);

        quantityEditText = findViewById(R.id.quantityEditText);
        pickupDateSpinner = findViewById(R.id.pickupDateSpinner);
        priceTextView = findViewById(R.id.priceTextView);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        category = dbHelper.getCategoryByName(categoryName);
        if (category != null) {
            categoryPrice = category.getPrice();
        }

        populatePickupDateSpinner();

        quantityEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePrice();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        placeOrderButton.setOnClickListener(v -> showOrderConfirmationDialog());
    }

    private void updatePrice() {
        String quantityStr = quantityEditText.getText().toString();
        if (!TextUtils.isEmpty(quantityStr)) {
            int quantity = Integer.parseInt(quantityStr);
            int totalPrice = categoryPrice * quantity;
            priceTextView.setText("Price: LKR. " + totalPrice);
        } else {
            priceTextView.setText("Price: LKR. 0");
        }
    }

    private void showOrderConfirmationDialog() {
        String quantityStr = quantityEditText.getText().toString();
        String pickupDate = pickupDateSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(pickupDate)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        int totalPrice = categoryPrice * quantity;

        new AlertDialog.Builder(this)
                .setTitle("Confirm Order")
                .setMessage("Total Price: LKR. " + totalPrice + "\nPickup Date: " + pickupDate)
                .setPositiveButton("Place Order", (dialog, which) -> placeOrder(quantity, pickupDate))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void placeOrder(int quantity, String pickupDate) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to place an order", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = sessionManager.getUserName();

        Order order = new Order("Username: " + username + ",Category: " + category.getName() + ", Quantity: " + quantity
                + ", Pickup Date: " + pickupDate + ", Price: LKR. " + categoryPrice * quantity + ", Status: pending");
        boolean isOrderPlaced = dbHelper.createOrder(order);

        if (isOrderPlaced) {
            new AlertDialog.Builder(this)
                    .setTitle("Order Success")
                    .setMessage("Order placed successfully. You can pick up your order on " + pickupDate)
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Order Failed")
                    .setMessage("Sorry, we cannot place orders right now. Please try again in a few hours.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void populatePickupDateSpinner() {
        List<String> dateOptions = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        for (int i = 0; i < 7; i++) {
            dateOptions.add(new java.text.SimpleDateFormat("EEEE").format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickupDateSpinner.setAdapter(adapter);
    }
}
