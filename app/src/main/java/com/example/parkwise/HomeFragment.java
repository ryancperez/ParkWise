package com.example.parkwise;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private static final String map_type_key = "67682c525b346928";



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
        // Set custom info window adapter
        Adapter infoWindowAdapter = new Adapter(requireContext());
        map.setInfoWindowAdapter(infoWindowAdapter);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.parkwiseday));

            if (!success) {
                Log.e("HomeFragment", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("HomeFragment", "Can't find style. Error: ", e);
        }

        LatLng southwest = new LatLng(34.235407, -118.533842); // Replace with actual southwest coordinates
        LatLng northeast = new LatLng(34.257348, -118.523345); // Replace with actual northeast coordinates

        LatLngBounds csunBounds = new LatLngBounds(southwest, northeast);

        BitmapDescriptor lot = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED); //icon for a open-space parking lot
        BitmapDescriptor struct = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW); //icon for a parking structure
        BitmapDescriptor both = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE); // icon for both open and structure

        // Adding parking lot markers
        LatLng b1 = new LatLng(34.236075, -118.533553);
        addParkingLotMarker(b1, "B1", 480, lot); // Add more similarly, DOUBLE CHECK CAPACITIES FOR ALL
        LatLng b3 = new LatLng(34.238009,-118.532780);
        addParkingLotMarker(b3, "B3", 2063, both);
        LatLng b5 = new LatLng(34.241317, -118.533330);
        addParkingLotMarker(b5, "B5", 1361, both);
        LatLng b6 = new LatLng(34.242900, -118.532145);
        addParkingLotMarker(b6, "B6", 734, lot);
        LatLng e6 = new LatLng(34.244430, -118.528835);
        addParkingLotMarker(e6, "E6", 448, lot);
        LatLng f10 = new LatLng(34.251720, -118.527135);
        addParkingLotMarker(f10, "F10", 890, lot);
        LatLng g3 = new LatLng(34.237761, -118.524382);
        addParkingLotMarker(g3, "G3", 979, lot);
        LatLng g3S= new LatLng(34.238594, -118.524844);
        addParkingLotMarker(g3S, "G3 Structure", 1000, struct); // G3 STRUCTURE CAPACITY UNKNOWN
        LatLng g4 = new LatLng(34.240732, -118.523969);
        addParkingLotMarker(g4, "G4", 1132, lot);
        LatLng f5 = new LatLng(34.241410, -118.524731);
        addParkingLotMarker(f5, "F5", 1000, lot); // F5 CAPACITY UNKNOWN
        LatLng g6 = new LatLng(34.243144, -118.523444);
        addParkingLotMarker(g6,"G6", 1000, struct); //G6 CAPACITY UNKNOWN

        // Move camera to fit CSUN bounds with padding
        int padding = 27; // Adjust padding as needed
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(csunBounds, padding));

        map.setOnMarkerClickListener(marker -> {
            // Show the info window when the marker is clicked
            marker.showInfoWindow();
            // Get the position of the marker
            // Specify a fixed latitude offset (adjust this value as needed)
            double latOffsetDegrees = 0.007;
            LatLng markerPosition = marker.getPosition();
            LatLng newTargetPosition = new LatLng(markerPosition.latitude + latOffsetDegrees, markerPosition.longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(newTargetPosition, 15f));

            // Return false to indicate that we didn't consume the event yet
            // This will allow the default behavior to occur (showing the info window)
            return true;
        });

        map.setOnInfoWindowClickListener(marker -> {
            // Launch Google Maps app with directions to the marker's location
            String uri = "http://maps.google.com/maps?daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });
    }


    // method for adding parking lot markers
    private void addParkingLotMarker(LatLng position, String lotName, int availableStalls, BitmapDescriptor icon) {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title("Lot " + lotName + " - Available Stalls: " + availableStalls).icon(icon);
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
            //Toast.makeText(requireContext(), "Lot " + lotName + " - Available Stalls: " + availableStalls, Toast.LENGTH_LONG).show();
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

