package com.cupcakecorner.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.cupcakecorner.R;
import com.cupcakecorner.database.DatabaseHelper;
import com.cupcakecorner.models.Category;

public class CategoriesAdminActivity extends AppCompatActivity {

    private EditText editCategoryName, editCategoryPrice;
    private Button buttonAddCategory;
    private LinearLayout containerCategories;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_admin);

        editCategoryName = findViewById(R.id.edit_category_name);
        editCategoryPrice = findViewById(R.id.edit_category_price);
        buttonAddCategory = findViewById(R.id.button_add_category);
        containerCategories = findViewById(R.id.container_categories);

        databaseHelper = DatabaseHelper.getInstance(this);

        buttonAddCategory.setOnClickListener(v -> {
            String name = editCategoryName.getText().toString();
            String priceStr = editCategoryPrice.getText().toString();
            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(CategoriesAdminActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int price;
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(CategoriesAdminActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            Category category = new Category(name, price);
            boolean isCreated = databaseHelper.createCategory(category);
            if (isCreated) {
                Toast.makeText(CategoriesAdminActivity.this, "Category added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CategoriesAdminActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
            }
            loadCategories();
        });

        loadCategories();
    }

    private void loadCategories() {
        containerCategories.removeAllViews();
        for (Category category : databaseHelper.getAllCategories()) {
            View categoryView = getLayoutInflater().inflate(R.layout.item_category, containerCategories, false);

            TextView textCategoryName = categoryView.findViewById(R.id.text_category_name);
            TextView textCategoryPrice = categoryView.findViewById(R.id.text_category_price);
            Button buttonUpdate = categoryView.findViewById(R.id.button_update_category);
            Button buttonDelete = categoryView.findViewById(R.id.button_delete_category);

            textCategoryName.setText(category.getName());
            textCategoryPrice.setText(String.valueOf(category.getPrice()));

            buttonUpdate.setOnClickListener(v -> showUpdateDialog(category));

            buttonDelete.setOnClickListener(v -> {
                boolean isDeleted = databaseHelper.deleteCategory(category.getId());
                if (isDeleted) {
                    Toast.makeText(CategoriesAdminActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CategoriesAdminActivity.this, "Failed to delete category", Toast.LENGTH_SHORT).show();
                }
                loadCategories();
            });

            containerCategories.addView(categoryView);
        }
    }

    private void showUpdateDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Category");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_category, null);
        builder.setView(dialogView);

        EditText editDialogCategoryName = dialogView.findViewById(R.id.edit_dialog_category_name);
        EditText editDialogCategoryPrice = dialogView.findViewById(R.id.edit_dialog_category_price);

        editDialogCategoryName.setText(category.getName());
        editDialogCategoryPrice.setText(String.valueOf(category.getPrice()));

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = editDialogCategoryName.getText().toString();
            String newPriceStr = editDialogCategoryPrice.getText().toString();
            if (newName.isEmpty() || newPriceStr.isEmpty()) {
                Toast.makeText(CategoriesAdminActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int newPrice;
            try {
                newPrice = Integer.parseInt(newPriceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(CategoriesAdminActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            category.setName(newName);
            category.setPrice(newPrice);
            boolean isUpdated = databaseHelper.updateCategory(category);
            if (isUpdated) {
                Toast.makeText(CategoriesAdminActivity.this, "Category updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CategoriesAdminActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
            }
            loadCategories();
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
