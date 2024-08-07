package com.cupcakecorner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cupcakecorner.R;
import com.cupcakecorner.database.DatabaseHelper;
import com.cupcakecorner.database.SessionManager;
import com.cupcakecorner.models.User;

public class MyAccountActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, currentPasswordEditText, newPasswordEditText,
            confirmPasswordEditText;
    private Button saveButton, logoutButton;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_account);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        saveButton = findViewById(R.id.saveButton);
        logoutButton = findViewById(R.id.logoutButton);

        loadUserDetails();

        saveButton.setOnClickListener(v -> saveChanges());
        logoutButton.setOnClickListener(v -> logoutUser());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadUserDetails() {
        String username = sessionManager.getUserName();
        Log.d("MyAccountActivity", "User Name: " + username);

        User user = dbHelper.getUserByName(username);
        if (user != null) {
            Log.d("MyAccountActivity", "User found: " + user.getUsername());
            username = user.getUsername();
            String email = user.getEmail();

            usernameEditText.setText(username);
            emailEditText.setText(email);
        } else {
            Log.d("MyAccountActivity", "User not found");
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChanges() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Username and Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(currentPassword) || !TextUtils.isEmpty(newPassword)
                || !TextUtils.isEmpty(confirmPassword)) {
            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)
                    || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "All password fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dbHelper.verifyUserPassword(username, currentPassword)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dbHelper.updateUserPassword(sessionManager.getUserName(), newPassword)) {
                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Toast.makeText(this, "Account details updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        sessionManager.logoutUser();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MyAccountActivity.this, LoginActivity.class));
        finish();
    }
}
