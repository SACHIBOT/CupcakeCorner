package com.cupcakecorner.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.cupcakecorner.R;
import com.cupcakecorner.database.DatabaseHelper;
import com.cupcakecorner.models.Category;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        dbHelper = DatabaseHelper.getInstance(this);

        loadCategories();
    }

    private void loadCategories() {

        List<Category> categories = dbHelper.getAllCategories();
        TextView noCategoriesTextView = findViewById(R.id.noCategoriesTextView);
        LinearLayout menuLinearLayout = findViewById(R.id.menuLinearLayout);

        if (categories.isEmpty()) {
            noCategoriesTextView.setVisibility(View.VISIBLE);
            menuLinearLayout.setVisibility(View.GONE);
            return;
        } else {
            noCategoriesTextView.setVisibility(View.GONE);
            menuLinearLayout.setVisibility(View.VISIBLE);
        }

        menuLinearLayout.removeAllViews();

        for (Category category : categories) {
            Button categoryButton = new Button(this);
            categoryButton.setText(category.getName());
            categoryButton.setId(View.generateViewId());
            categoryButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            categoryButton.setTextSize(20);
            categoryButton.setTextColor(ContextCompat.getColor(this, R.color.black));
            categoryButton.setBackgroundResource(R.drawable.rounded_square);
            categoryButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));

            categoryButton.setTypeface(null, Typeface.BOLD);

            categoryButton.setOnClickListener(v -> {
                Intent intent = new Intent(CategoriesActivity.this, AddOrderActivity.class);
                intent.putExtra("categoryName", category.getName());
                startActivity(intent);
            });

            menuLinearLayout.addView(categoryButton);
        }
    }

}
