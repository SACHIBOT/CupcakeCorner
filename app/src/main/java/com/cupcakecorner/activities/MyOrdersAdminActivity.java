package com.cupcakecorner.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cupcakecorner.R;
import com.cupcakecorner.database.DatabaseHelper;
import com.cupcakecorner.models.Order;

import java.util.List;

public class MyOrdersAdminActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ViewGroup ordersContainer;
    private TextView noOrdersMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ordersContainer = findViewById(R.id.orders_container);
        noOrdersMessage = findViewById(R.id.no_orders_message);
        databaseHelper = DatabaseHelper.getInstance(this);

        loadOrders();
    }

    private void loadOrders() {
        ordersContainer.removeAllViews();
        List<Order> orders = databaseHelper.getAllOrders();

        if (orders.isEmpty()) {
            noOrdersMessage.setVisibility(View.VISIBLE);
            ordersContainer.setVisibility(View.GONE);
        } else {
            noOrdersMessage.setVisibility(View.GONE);
            ordersContainer.setVisibility(View.VISIBLE);

            for (Order order : orders) {
                View orderView = LayoutInflater.from(this).inflate(R.layout.order_item, ordersContainer, false);

                TextView text_user_name = orderView.findViewById(R.id.text_user_name);
                TextView text_category_name = orderView.findViewById(R.id.text_category_name);
                TextView text_quantity = orderView.findViewById(R.id.text_quantity);
                TextView text_pickup_date = orderView.findViewById(R.id.text_pickup_date);

                Button btnReady = orderView.findViewById(R.id.btn_ready);
                Button btnCollected = orderView.findViewById(R.id.btn_collected);
                Button btnDelete = orderView.findViewById(R.id.btn_delete);

                updateTextViews(order, text_user_name, text_category_name, text_quantity, text_pickup_date);

                updateButtonVisibility(order, btnReady, btnCollected, btnDelete);

                btnReady.setOnClickListener(v -> {
                    if (updateOrderStatus(order, "Status: ready")) {
                        Toast.makeText(this, "Order updated to ready", Toast.LENGTH_SHORT).show();
                        updateButtonVisibility(order, btnReady, btnCollected, btnDelete);
                    } else {
                        Toast.makeText(this, "Failed to update order", Toast.LENGTH_SHORT).show();
                    }
                });

                btnCollected.setOnClickListener(v -> {
                    if (updateOrderStatus(order, "Status: collected")) {
                        Toast.makeText(this, "Order updated to collected", Toast.LENGTH_SHORT).show();
                        updateButtonVisibility(order, btnReady, btnCollected, btnDelete);
                    } else {
                        Toast.makeText(this, "Failed to update order", Toast.LENGTH_SHORT).show();
                    }
                });

                btnDelete.setOnClickListener(v -> {
                    if (databaseHelper.deleteOrder(order.getId())) {
                        Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show();
                        ordersContainer.removeView(orderView);
                        loadOrders();
                    } else {
                        Toast.makeText(this, "Failed to delete order", Toast.LENGTH_SHORT).show();
                    }
                });

                ordersContainer.addView(orderView);
            }
        }
    }

    private boolean updateOrderStatus(Order order, String newStatus) {
        String currentDetails = order.getDetails();
        String updatedDetails = currentDetails;

        if (newStatus.equals("Status: ready") && !currentDetails.contains("Status: ready")) {
            if (currentDetails.contains("Status: pending")) {
                updatedDetails = currentDetails.replace("Status: pending", "Status: ready");
            } else {
                updatedDetails = currentDetails + " , Status: ready";
            }
        } else if (newStatus.equals("Status: collected") && !currentDetails.contains("Status: collected")) {
            updatedDetails = currentDetails.replace("Status: ready", "Status: collected");
        } else {
            return false;
        }

        order.setDetails(updatedDetails);
        return databaseHelper.updateOrder(order);
    }

    public void updateTextViews(Order order, TextView text_user_name, TextView text_category_name, TextView text_quantity, TextView text_pickup_date) {
        order.parseDetails(order.getDetails());
        text_user_name.setText("User: " + order.getUsername());
        text_category_name.setText("Category: " + order.getCategory());
        text_quantity.setText("Quantity: " + order.getQuantity());
        text_pickup_date.setText("Pickup Date: " + order.getPickupDate());
    }

    private void updateButtonVisibility(Order order, Button btnReady, Button btnCollected, Button btnDelete) {
        String currentDetails = order.getDetails();
        if (currentDetails.contains("Status: pending")) {
            btnReady.setVisibility(View.VISIBLE);
            btnCollected.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        } else if (currentDetails.contains("Status: ready")) {
            btnReady.setVisibility(View.GONE);
            btnCollected.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        } else if (currentDetails.contains("Status: collected")) {
            btnReady.setVisibility(View.GONE);
            btnCollected.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        }
    }
}
