package com.example.parkwise;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class Adapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private final View mButtonView;

    public Adapter(Context context){
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        mButtonView = LayoutInflater.from(context).inflate(R.layout.navigation_button, null);
    }


    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    public View getButtonView() {
        return mButtonView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        titleTextView.setText(marker.getTitle());

    }
}