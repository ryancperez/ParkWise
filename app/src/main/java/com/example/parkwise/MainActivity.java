package com.example.parkwise;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //ActivityMainBinding binding;
    private EditText username;
    private EditText password;
    private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 700;
    private String GEOFENCE_ID = "CSUN";
    LatLng CSUN = new LatLng(34.2408, -118.5301);
    private GeoFenceHelper geoFenceHelper;
    private boolean loginAccepted = false;
    LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);


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
                            geofencingClient = LocationServices.getGeofencingClient(this);
                            geoFenceHelper = new GeoFenceHelper(this);

                            addGeofence(CSUN, GEOFENCE_RADIUS);
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

                loadingDialog.startLoadingDialog();
                forcecloseDialog(5000);
                login();

//                if (loginAccepted)
//                    openMenu();

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
        Handler handler = new Handler();
        handler.post(() -> {
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
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", usernameText);
                        editor.apply();
                        loadingDialog.dismissDialog();
                        openMenu();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("You have logged in");
                                loginAccepted = true;
                            }
                        });
                    } else {
                        // No matching user found, show an error message
                        loadingDialog.dismissDialog();
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
        });

    }

    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geoFenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geoFenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geoFenceHelper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geoFenceHelper.geoErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    private void forcecloseDialog(long milli){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        }, milli);
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}