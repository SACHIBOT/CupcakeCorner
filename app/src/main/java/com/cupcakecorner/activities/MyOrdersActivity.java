package com.cupcakecorner.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cupcakecorner.R;
import com.cupcakecorner.database.DatabaseHelper;
import com.cupcakecorner.models.Order;

import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ViewGroup ordersLinearLayout;
    private TextView noOrdersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);

        dbHelper = DatabaseHelper.getInstance(this);
        ordersLinearLayout = findViewById(R.id.ordersLinearLayout);
        noOrdersTextView = findViewById(R.id.noOrdersTextView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = dbHelper.getAllOrders();
        if (orders.isEmpty()) {
            noOrdersTextView.setVisibility(View.VISIBLE);
        } else {
            noOrdersTextView.setVisibility(View.GONE);
            for (Order order : orders) {
                View orderView = createOrderView(new Order(order.getDetails()));
                ordersLinearLayout.addView(orderView);
            }
        }
    }

    private View createOrderView(Order order) {
        View orderView = LayoutInflater.from(this).inflate(R.layout.item_order, ordersLinearLayout, false);

        TextView categoryTextView = orderView.findViewById(R.id.categoryTextView);
        TextView quantityTextView = orderView.findViewById(R.id.quantityTextView);
        TextView priceTextView = orderView.findViewById(R.id.priceTextView);
        TextView pickupDateTextView = orderView.findViewById(R.id.pickupDateTextView);

        categoryTextView.setText(order.getCategory());
        quantityTextView.setText("Quantity: " + order.getQuantity());
        priceTextView.setText("Price: LKR. " + order.getTotalPrice());
        pickupDateTextView.setText("Pickup Date: " + order.getPickupDate());

        return orderView;
    }
}
