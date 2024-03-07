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
            String time = intent.getStringExtra("time");
            String car = intent.getStringExtra("car");
            int image = intent.getIntExtra("image", R.drawable.user_filler);
            int id = intent.getIntExtra("id", 1);

            binding.detailName.setText(name);

        }
    }
}