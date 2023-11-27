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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.mapContainer, mapFragment).commit();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add markers for each parking lot
        LatLng b5LatLng = new LatLng(34.241594, -118.532754);
        addParkingLotMarker(b5LatLng, "B5", 50); // Add more similarly.

        map.setOnMarkerClickListener(marker -> {
            // Display parking lot details in window
            showLotInfo(marker);
            return false;
        });

        // Setting coords to focus on CSUN campus using CameraUpdate
        LatLng csunLatLng = new LatLng(34.2419, -118.5283); // CSUN's approximate center coordinates
        float zoomLevel = 16f; // Adjust the zoom level as needed

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(csunLatLng, zoomLevel);
        map.animateCamera(cameraUpdate);
    }

    // method for adding parking lot markers
    private void addParkingLotMarker(LatLng position, String lotName, int availableStalls) {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title("Parking Lot " + lotName);
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