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

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

        LatLng csunLatLng = new LatLng(34.2419, -118.5283); // CSUN's approximate center coordinates
        float zoomLevel = 15f; // Fixed zoom level for CSUN's campus

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(csunLatLng, zoomLevel));

        map.setOnMarkerClickListener(marker -> {
            showLotInfo(marker);
            return false;
        });

        // Adding parking lot markers
        LatLng b5LatLng = new LatLng(34.241594, -118.532754);
        addParkingLotMarker(b5LatLng, "B5", 50); // Add more similarly
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