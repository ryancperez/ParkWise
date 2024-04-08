package com.example.parkwise;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

public class Adapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private final View mButtonView;

    private Map<Marker, Integer> mMarkerImageMap; // map to store marker-image relationships

    public Adapter(Context context){
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        mButtonView = LayoutInflater.from(context).inflate(R.layout.navigation_button, null);
        mMarkerImageMap = new HashMap<>(); // marker - image map
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
        ImageView imageView = view.findViewById(R.id.infoWindowDefault);

        ParkingLotDetails details = (ParkingLotDetails) marker.getTag();
        if (details != null) {
            // set title
            titleTextView.setText("Lot " + details.getLotName() + " - Available Stalls: " + details.getAvailableStalls());

            // Set image dynamically based on marker
            Integer imageResource = mMarkerImageMap.get(marker);
            if (imageResource != null) {
                imageView.setImageResource(imageResource);
            }
            else {
                imageView.setImageResource(R.drawable.parkwise_bg); // default parking lot img
            }
        } else {
            titleTextView.setText("ParkWise");
        }
    }

    // Method to add marker-image association to the map
    public void addMarkerImage(Marker marker, int imageResource) {
        mMarkerImageMap.put(marker, imageResource);
    }

    // Method to remove marker-image association from the map
    public void removeMarkerImage(Marker marker) {
        mMarkerImageMap.remove(marker);
    }
}