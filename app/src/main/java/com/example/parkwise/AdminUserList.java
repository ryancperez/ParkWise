package com.example.parkwise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parkwise.databinding.ActivityAdminUserListBinding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdminUserList extends AppCompatActivity {

    ActivityAdminUserListBinding binding;
    ListAdapter listAdapter;
    ArrayList<UserData> dataArrayList = new ArrayList<>();
    UserData userData;

    LoadingDialog loadingDialog = new LoadingDialog(AdminUserList.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getUsers();
        loadingDialog.startLoadingDialog();
        forcecloseDialog(2000);
        listAdapter = new ListAdapter(AdminUserList.this, dataArrayList);
        binding.listview.setAdapter(listAdapter);
        binding.listview.setClickable(true);

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserData user = dataArrayList.get(i);
                Intent intent = new Intent(AdminUserList.this, UserInformation.class);
                intent.putExtra("name", user.firstName + " " + user.lastName);
                intent.putExtra("email", user.email);
                intent.putExtra("role", user.role);
                intent.putExtra("image", user.profileImg);
                intent.putExtra("username", user.username);
                startActivity(intent);

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

    void getUsers(){
        DatabaseConnector dbConnector = new DatabaseConnector();
        String sql = "SELECT * FROM USER;";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery();) {
                while(resultSet.next()){
                    UserData user = new UserData(
                            resultSet.getString("username"),
                            R.drawable.user_filler,
                            resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("email"),
                            resultSet.getString("role")
                    );
                    dataArrayList.add(user);
                }
        }
        catch (SQLException e) {e.printStackTrace();}
//        loadingDialog.dismissDialog();
    }
}


