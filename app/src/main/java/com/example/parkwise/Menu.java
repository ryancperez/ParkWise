package com.example.parkwise;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parkwise.databinding.ActivityMainBinding;

public class Menu extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.settings) {
                replaceFragment(new SettingsFragment());
            } else if (itemId == R.id.payment){
                replaceFragment(new Payment());
            } else if (itemId == R.id.VIP) {
                replaceFragment(new vipPayment());
            }



            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void switchToPayment(Payment paymentFragment) {
        // Switch to the desired fragment based on your logic
        // For example:
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentFragment)
                .commit();
        binding.bottomNavigationView.setSelectedItemId(R.id.payment);
    }

    public void switchToVIP(vipPayment fragment) {
        // Switch to the desired fragment based on your logic
        // For example:
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        binding.bottomNavigationView.setSelectedItemId(R.id.VIP);
    }


}
