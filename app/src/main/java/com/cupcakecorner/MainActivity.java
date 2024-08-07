package com.cupcakecorner;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.cupcakecorner.activities.HomeActivityAdmin;
import com.cupcakecorner.activities.HomeActivityUser;
import com.cupcakecorner.activities.LoginActivity;
import com.cupcakecorner.activities.SignupActivity;
import com.cupcakecorner.database.SessionManager;
import com.cupcakecorner.models.admin;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            if (new admin().getAdminUsername().equals(sessionManager.getUserName())) {
                Intent intent = new Intent(MainActivity.this, HomeActivityAdmin.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, HomeActivityUser.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
