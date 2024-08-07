package com.cupcakecorner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cupcakecorner.R;

public class HomeActivityAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnCategoriesAdmin = findViewById(R.id.btnCategoriesAdmin);
        Button btnMyOrdersAdmin = findViewById(R.id.btnMyOrdersAdmin);
        Button btnMyAccountUser = findViewById(R.id.btnMyAccountUser);
        Button btnSpecialRequestsAdmin = findViewById(R.id.btnSpecialRequestsAdmin);

        btnCategoriesAdmin.setOnClickListener(v -> openActivity(CategoriesAdminActivity.class));
        btnMyOrdersAdmin.setOnClickListener(v -> openActivity(MyOrdersAdminActivity.class));
        btnMyAccountUser.setOnClickListener(v -> openActivity(MyAccountActivity.class));
        btnSpecialRequestsAdmin.setOnClickListener(v -> openActivity(SpecialRequestsActivity.class));
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(HomeActivityAdmin.this, activityClass);
        startActivity(intent);
    }

}