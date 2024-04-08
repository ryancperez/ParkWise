package com.example.parkwise;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    //ActivityMainBinding binding;
    private EditText username;
    private EditText password;

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        Boolean backgroundLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,false);
                        if (backgroundLocationGranted != null && backgroundLocationGranted){

                        }
                        else if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                        } else {
                            // No location access granted.
                        }
                    }
            );

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Initialize UI elements

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        locationPermissionRequest.launch(new String[] {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        });

        Button loginButton = findViewById(R.id.LoginUpButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login();
                openMenu();
            }
        });

        ImageButton goToSignUpButton = findViewById(R.id.GoToSignUpButton);
        ImageButton goToAdminLogin = findViewById(R.id.adminLogin);

        goToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUp();
            }
        });

        goToAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAdminLogin();
            }
        });

    }
    private void openMenu(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    private void openSignUp(){
        Intent intent = new Intent(this, Sign_up.class);
        startActivity(intent);
    }

    private void openAdminLogin(){
        Intent intent = new Intent(this, AdminLogin.class);
        startActivity(intent);
    }

    private void login() {

        String usernameText = username.getText().toString();

        String passwordText = password.getText().toString();

        if (usernameText.isEmpty() || passwordText.isEmpty()) {
            showToast("Username and Password are required.");
            return;
        }



        // Create a new Thread to perform the signup operation
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseConnector dbConnector = new DatabaseConnector();
                    Connection conn = dbConnector.getConnection();
                    if (conn != null) {

                        // Insert the new user data into the database
                        String sql = "SELECT * FROM USER WHERE username = ? AND password = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, usernameText);
                        pstmt.setString(2, passwordText);
                        ResultSet resultSet = pstmt.executeQuery();


                        if (resultSet.next()) {
                            // Matching user found, show a success message
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("You have logged in");
                                }
                            });
                        } else {
                            // No matching user found, show an error message
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Incorrect password or username.Please try again");
                                }
                            });
                        }

                        resultSet.close();
                        pstmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Sign-in failed. Please try again.");
                        }
                    });
                }
            }
        }).start();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}