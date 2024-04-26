package com.example.parkwise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<UserData> {
    public ListAdapter(@NonNull Context context, ArrayList<UserData> dataArrayList) {
        super(context, R.layout.user_list, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent){
        UserData userData = getItem(position);

        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_list, parent, false);

        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listName = view.findViewById(R.id.listname);
        TextView listTime = view.findViewById(R.id.listTime);

        listImage.setImageResource(userData.profileImg);
        listName.setText(userData.firstName + " " + userData.lastName);
        listTime.setText(userData.role);

        return view;
    }

}
