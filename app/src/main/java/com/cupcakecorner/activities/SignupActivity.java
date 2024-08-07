package com.cupcakecorner.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cupcakecorner.R;
import com.cupcakecorner.database.DatabaseHelper;
import com.cupcakecorner.database.SessionManager;
import com.cupcakecorner.models.User;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button signupButton;
    private Button loginButton;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.usernameRegisterText);
        passwordEditText = findViewById(R.id.passwordLoginText);
        emailEditText = findViewById(R.id.emailRegisterText);
        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButtonsignup);
        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registerUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }else{
        if (dbHelper.checkUser(username, email)) {
            Toast.makeText(this, "Username or email already exists", Toast.LENGTH_SHORT).show();
        } else {
            User newUser = new User(username, password, email);
            boolean registered = dbHelper.createUser(newUser);
            if (!registered) {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            } else {
                sessionManager.createLoginSession(newUser.getUsername());
                Intent intent = new Intent(SignupActivity.this, HomeActivityUser.class);
                Toast.makeText(this, "Registration Success", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
    }


    }
}
