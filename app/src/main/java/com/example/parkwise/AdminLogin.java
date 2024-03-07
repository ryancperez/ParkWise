package com.example.parkwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        ImageButton BackToUserLogin = findViewById(R.id.userLoginButton);
        Button AdminMenu = findViewById(R.id.LoginUpButton);

        AdminMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAdminMenu();
            }
        });
        BackToUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserLogin();
            }
        });
    }

    private void openUserLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openAdminMenu(){
        Intent intent = new Intent(this, AdminUserList.class);
        startActivity(intent);
    }

}