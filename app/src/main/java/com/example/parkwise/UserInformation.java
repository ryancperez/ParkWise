package com.example.parkwise;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parkwise.databinding.ActivityUserInformationBinding;

public class UserInformation extends AppCompatActivity {

    ActivityUserInformationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = this.getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String username = intent.getStringExtra("username");
            String email = intent.getStringExtra("email");
            String role = intent.getStringExtra("role");
            String lpNumber = intent.getStringExtra("LPnumber");
            String carInfo = intent.getStringExtra("carMake") + " " + intent.getStringExtra("carColor");
            int image = intent.getIntExtra("image", R.drawable.user_filler);
//            int id = intent.getIntExtra("id", 1);

            binding.detailName.setText(name);
            binding.usernameInfo.setText("Username: " + username);
            binding.emailInfo.setText("Email: " + email);
            binding.roleInfo.setText("Role: " + role);
            binding.carInfo.setText("Car: " + carInfo);
            binding.licensePlateInfo.setText("LP #: " + lpNumber);


        }
    }
}