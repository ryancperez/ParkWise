package com.example.parkwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parkwise.databinding.ActivityAdminUserListBinding;

import java.util.ArrayList;

public class AdminUserList extends AppCompatActivity {

    ActivityAdminUserListBinding binding;
    ListAdapter listAdapter;
    ArrayList<UserData> dataArrayList = new ArrayList<>();
    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int[] imageList = {R.drawable.user_filler, R.drawable.user_filler, R.drawable.user_filler, R.drawable.user_filler, R.drawable.user_filler};
        int[] idList = {1,2,3,4,5};
        String[] fnamelist = {"Ryan", "Sevada", "Summer", "Efrin", "Adrian"};
        String[] lnameList = {"P", "G", "S", "E", "V"};
        String[] timeList = {"30min", "1hr", "2hrs", "45min", "15 min"};
        String[] carList = {"GTI", "Kia", "Subaru", "Chevy", "Subaru"};

        for (int i = 0; i< imageList.length; i++){
            dataArrayList.add(userData);
        }
        listAdapter = new ListAdapter(AdminUserList.this, dataArrayList);
        binding.listview.setAdapter(listAdapter);
        binding.listview.setClickable(true);

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AdminUserList.this, UserInformation.class);
                intent.putExtra("name", fnamelist[i] + " " + lnameList[i]);
                intent.putExtra("time", timeList[i]);
                intent.putExtra("id", idList[i]);
                intent.putExtra("image", imageList[i]);
                intent.putExtra("car", carList[i]);
                startActivity(intent);

            }
        });

    }
}