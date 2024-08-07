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
import com.cupcakecorner.models.admin;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameLoginText);
        passwordEditText = findViewById(R.id.passwordLoginText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.SignUpButtonLogin);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUser(username, password);
        if (user != null) {
            sessionManager.createLoginSession(user.getUsername());
            if(new admin().checkAdmin(user.getUsername(),user.getPassword(),user.getEmail())){
                Intent intent = new Intent(LoginActivity.this, HomeActivityAdmin.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(LoginActivity.this, HomeActivityUser.class);
                startActivity(intent);
            }
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
