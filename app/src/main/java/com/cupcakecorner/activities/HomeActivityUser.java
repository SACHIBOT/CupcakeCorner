package com.cupcakecorner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cupcakecorner.R;

public class HomeActivityUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnCategoriesUser = findViewById(R.id.btnCategoriesUser);
        Button btnMyOrdersUser = findViewById(R.id.btnMyOrdersUser);
        Button btnMyAccountUser = findViewById(R.id.btnMyAccountUser);
        Button btnSpecialRequestsUser = findViewById(R.id.btnSpecialRequestsUser);

        btnCategoriesUser.setOnClickListener(v -> openActivity(CategoriesActivity.class));
        btnMyOrdersUser.setOnClickListener(v -> openActivity(MyOrdersActivity.class));
        btnMyAccountUser.setOnClickListener(v -> openActivity(MyAccountActivity.class));
        btnSpecialRequestsUser.setOnClickListener(v -> openActivity(SpecialRequestsActivity.class));
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(HomeActivityUser.this, activityClass);
        startActivity(intent);
    }
}
