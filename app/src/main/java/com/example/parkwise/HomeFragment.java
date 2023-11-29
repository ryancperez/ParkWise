package com.example.parkwise;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment mapFragment;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Obtain the SupportMapFragment asynchronously
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.mapContainer, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this); // This triggers onMapReady when the map is ready

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng southwest = new LatLng(34.235407, -118.533842); // Replace with actual southwest coordinates
        LatLng northeast = new LatLng(34.257348, -118.523345); // Replace with actual northeast coordinates

        LatLngBounds csunBounds = new LatLngBounds(southwest, northeast);

        // Adding parking lot markers
        LatLng b1 = new LatLng(34.236075, -118.533553);
        addParkingLotMarker(b1, "B1", 480); // Add more similarly
        LatLng b3 = new LatLng(34.238009,-118.532780);
        addParkingLotMarker(b3, "B3", 2063);
        LatLng b5 = new LatLng(34.241317, -118.533330);
        addParkingLotMarker(b5, "B5", 1361);
        LatLng b6 = new LatLng(34.242847, -118.532735);
        addParkingLotMarker(b6, "B6", 734);
        LatLng e6 = new LatLng(34.244408, -118.528443);
        addParkingLotMarker(e6, "E6", 448);




        // Move camera to fit CSUN bounds with padding
        int padding = 27; // Adjust padding as needed
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(csunBounds, padding));

        map.setOnMarkerClickListener(marker -> {
            // Show the info window when the marker is clicked
            marker.showInfoWindow();

            // Return false to indicate that we didn't consume the event yet
            // This will allow the default behavior to occur (showing the info window)
            return false;
        });

        map.setOnInfoWindowClickListener(marker -> {
            // When the info window is tapped, zoom in on the marker's position
            LatLng markerLoc = marker.getPosition();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(markerLoc, 17.5f));

            // Perform other actions or show information related to the clicked marker
            showLotInfo(marker);
        });
    }


    // method for adding parking lot markers
    private void addParkingLotMarker(LatLng position, String lotName, int availableStalls) {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title("Lot " + lotName);
        map.addMarker(markerOptions).setTag(new ParkingLotDetails(lotName, availableStalls));
    }

    private void showLotInfo(Marker marker) {
        ParkingLotDetails details = (ParkingLotDetails) marker.getTag();
        if (details != null) {
            // Display details in a custom info window or dialog
            // For example, use a custom layout or AlertDialog to show lot number and available stalls
            String lotName = details.getLotName();
            int availableStalls = details.getAvailableStalls();

            // Example: Use a Toast to display details
            Toast.makeText(requireContext(), "Lot " + lotName + " - Available Stalls: " + availableStalls, Toast.LENGTH_LONG).show();
        }
    }

}

// Class to hold parking lot details
class ParkingLotDetails {
    private String lotName;
    private int availableStalls;

    ParkingLotDetails(String lotName, int availableStalls) {
        this.lotName = lotName;
        this.availableStalls = availableStalls;
    }

    String getLotName() {
        return lotName;
    }

    int getAvailableStalls() {
        return availableStalls;
    }
}

