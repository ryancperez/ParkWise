package com.example.parkwise;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    //ActivityMainBinding binding;
    private EditText username;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Initialize UI elements

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);


        Button signUp = findViewById(R.id.LoginUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login();
                openMenu();
            }
        });


    }
    private void openMenu(){
        Intent intent = new Intent(this, Menu.class);
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