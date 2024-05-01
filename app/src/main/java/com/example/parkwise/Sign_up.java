package com.example.parkwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Sign_up extends AppCompatActivity {

    private EditText carInfo;
    private EditText lpNumber;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize UI elements
        carInfo = findViewById(R.id.carInfo);
        lpNumber = findViewById(R.id.lpNumber);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);

        Button signUp = findViewById(R.id.SignUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
                OpenLogin();
            }
        });
    }

    private void OpenLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void signup() {
        String carInfoText = carInfo.getText().toString();
        String lpNumberText = lpNumber.getText().toString();
        String usernameText = username.getText().toString();
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();
        String[] nameInfo = emailText.split("\\.");
        String[] carInfo = carInfoText.split(" ");

        if (usernameText.isEmpty() || emailText.isEmpty() || carInfoText.isEmpty() || lpNumberText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
            showToast("All fields are required.");
            return;
        }

        if (!passwordText.equals(confirmPasswordText)) {
            showToast("Passwords don't match.");
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

                        String checkQuery = "SELECT * FROM USER WHERE username = ? OR email = ?";
                        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                        checkStmt.setString(1, usernameText);
                        checkStmt.setString(2, emailText);
                        ResultSet resultSet = checkStmt.executeQuery();

                        // If a result is returned, it means the username or email is already taken
                        if (resultSet.next()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Username or email already exists.");
                                }
                            });
                            resultSet.close();
                            checkStmt.close();
                            return;
                        }



                        //Statement stmt = conn.createStatement();
                        String sql = "INSERT INTO USER VALUES (?, ?, ?, ?, ?, ?, false, ?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, usernameText);
                        pstmt.setString(2, passwordText);
                        pstmt.setString(3, nameInfo[0]);
                        pstmt.setString(4, nameInfo[1]);
                        pstmt.setString(5, emailText);
                        pstmt.setString(6, "student");
                        pstmt.setString(7, lpNumberText);
                        pstmt.setString(8, carInfo[0]);
                        pstmt.setString(9, carInfo[1]);


                        // Execute the query and handle any exceptions
                        pstmt.executeUpdate();

                        // Don't forget to close the connection and statements when done.
                        pstmt.close();
                        //stmt.close();

                        // Update the UI using runOnUiThread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Sign-up successful!");
                            }
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Sign-up failed. Please try again.");
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
