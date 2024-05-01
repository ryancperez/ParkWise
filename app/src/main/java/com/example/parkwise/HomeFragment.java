package com.example.parkwise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeFragment";
    private GoogleMap map;
    private SupportMapFragment mapFragment;
//    private GeofencingClient geofencingClient;
    private final float GEOFENCE_RADIUS = 400;

    private Adapter infoWindowAdapter;
    private static final String map_type_key = "67682c525b346928";
    private String username;
    SharedPreferences sharedPreferences;
    private CountDownTimer timer;
    long endtimeMS = -1;
    TextView countDownTimer;



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

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "default_value");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setTimer();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
            countDownTimer = view.findViewById(R.id.timer);
        // Obtain the SupportMapFragment asynchronously
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.mapContainer, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this); // This triggers onMapReady when the map is ready

//        startTimer();
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Set custom info window adapter
        infoWindowAdapter = new Adapter(requireContext());
        map.setInfoWindowAdapter(infoWindowAdapter);
        enableUserLocation();
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

        BitmapDescriptor lot = BitmapDescriptorFactory.fromResource(R.drawable.csunmarkerred); //icon for a open-space parking lot
        BitmapDescriptor struct = BitmapDescriptorFactory.fromResource(R.drawable.csunmarkerorange); //icon for a parking structure
        BitmapDescriptor both = BitmapDescriptorFactory.fromResource(R.drawable.csunmarkerblack); // icon for both open and structure
        BitmapDescriptor parkwiselot = BitmapDescriptorFactory.fromResource(R.drawable.csunmarkerpw);

        // Adding parking lot markers
        LatLng b1 = new LatLng(34.236075, -118.533553);
        addParkingLotMarker(b1, "B1", 480, lot, R.drawable.b1); // Add more similarly, DOUBLE CHECK CAPACITIES FOR ALL
        LatLng b3 = new LatLng(34.238009,-118.532780);
        addParkingLotMarker(b3, "B3", 2063, both, R.drawable.b3);
        LatLng b5 = new LatLng(34.241317, -118.533330);
        addParkingLotMarker(b5, "B5", 1361, both, R.drawable.b5);
        LatLng b6 = new LatLng(34.242900, -118.532145);
        addParkingLotMarker(b6, "B6", 734, lot, R.drawable.b6);
        LatLng e6 = new LatLng(34.244430, -118.528835);
        addParkingLotMarker(e6, "E6", 448, lot, R.drawable.e6);
        LatLng f10 = new LatLng(34.251720, -118.527135);
        addParkingLotMarker(f10, "F10", 890, parkwiselot, R.drawable.f10);
        LatLng g3 = new LatLng(34.237761, -118.524382);
        addParkingLotMarker(g3, "G3", 979, lot, R.drawable.g3);
        LatLng g3S= new LatLng(34.238594, -118.524844);
        addParkingLotMarker(g3S, "G3 Structure", 1000, struct, R.drawable.g3s); // G3 STRUCTURE CAPACITY UNKNOWN
        LatLng g4 = new LatLng(34.240732, -118.523969);
        addParkingLotMarker(g4, "G4", 1132, lot, R.drawable.g4);
        LatLng f5 = new LatLng(34.241410, -118.524731);
        addParkingLotMarker(f5, "F5", 1000, lot, R.drawable.f5); // F5 CAPACITY UNKNOWN
        LatLng g6 = new LatLng(34.243144, -118.523444);
        addParkingLotMarker(g6,"G6", 1000, struct, R.drawable.g6); //G6 CAPACITY UNKNOWN

        // Move camera to fit CSUN bounds with padding
        int padding = 27; // Adjust padding as needed
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(csunBounds, padding));

        map.setOnMarkerClickListener(marker -> {
            // Show the info window when the marker is clicked
            marker.showInfoWindow();

            // Remove button view from ParentView if already added (reset button basically)
            ViewGroup parentView = (ViewGroup) mapFragment.getView();
            View buttonView = infoWindowAdapter.getButtonView();
            if (parentView != null && parentView.indexOfChild(buttonView) != -1) {
                parentView.removeView(buttonView);
            }

            // Specify a fixed latitude offset (adjust this value as needed)
            double latOffsetDegrees = 0.007;
            LatLng markerPosition = marker.getPosition();
            LatLng newTargetPosition = new LatLng(markerPosition.latitude + latOffsetDegrees, markerPosition.longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(newTargetPosition, 15f), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    // Camera movement finished, add the navigation button view
                    View buttonView = infoWindowAdapter.getButtonView();
                    ViewGroup parentView = (ViewGroup) mapFragment.getView(); // Assuming you are using a SupportMapFragment
                    if (parentView != null) {
                        parentView.addView(buttonView);
                    }

                    buttonView.findViewById(R.id.navigateButton).setOnClickListener(v -> {
                        // Launch Google Maps app with directions to the marker's location
                        String uri = "http://maps.google.com/maps?daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                    });
                }

                @Override
                public void onCancel() {
                    // Camera movement canceled, do nothing or handle the scenario accordingly
                }
            });

            return true;
        });

        map.setOnInfoWindowClickListener(marker -> {
            ParkingLotDetails details = (ParkingLotDetails)marker.getTag();
            String lotName = null;
                if (details != null) {
                    // Display details in a custom info window or dialog
                    // For example, use a custom layout or AlertDialog to show lot number and available stalls
                    lotName = details.getLotName();
                }
                else {
                    lotName = "ParkWise";
                }
            Bundle bundle = new Bundle();
            bundle.putString("lotName", lotName);

            Payment paymentFragment = new Payment();
            paymentFragment.setArguments(bundle);

            // Send a signal to the activity indicating which fragment to switch to
            if (getActivity() instanceof Menu) {
                ((Menu) getActivity()).switchToPayment(paymentFragment);
            }
        });

        map.setOnMapClickListener(latLng -> {
            // Remove the button view from its parent ViewGroup when clicking off a marker
            ViewGroup parentView = (ViewGroup) mapFragment.getView();
            View buttonView = infoWindowAdapter.getButtonView();
            if (parentView != null && parentView.indexOfChild(buttonView) != -1) {
                parentView.removeView(buttonView);
            }
        });

    }

    private void enableUserLocation(){
        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)){
            map.setMyLocationEnabled(true);
        }
        else{

        }
    }

    // method for adding parking lot markers
    private void addParkingLotMarker(LatLng position, String lotName, int availableStalls, BitmapDescriptor icon, int imageResource) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title("Lot " + lotName + " - Available Stalls: " + availableStalls)
                .icon(icon);

        Marker marker = map.addMarker(markerOptions);
        marker.setTag(new ParkingLotDetails(lotName, availableStalls));
        infoWindowAdapter.addMarkerImage(marker, imageResource);
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

    private void setTimer(){
        if (!username.equals("default_value")){
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DatabaseConnector dbConnector = new DatabaseConnector();
                    String sql = "SELECT end_datetime FROM ParkWise.time_table WHERE username = ? " +
                            "ORDER BY end_datetime DESC LIMIT 1;";
                    try (Connection conn = dbConnector.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql);) {

                        pstmt.setString(1, username);
                        try(ResultSet resultSet = pstmt.executeQuery();){
                            if (resultSet.next()) {
                                Log.d(TAG, "**GOT END_TIME**");
                                Timestamp endtime = resultSet.getTimestamp("end_datetime");
                                endtimeMS = endtime.getTime();
                                startTimer();
                                Log.d(TAG, "run: endtime = " + endtime.toString());
                            }
                            else{
                                Log.d(TAG, "**END_TIME NOT FOUND**");
                                endtimeMS = 0;
                            }
                        } catch (SQLException e) {e.printStackTrace(); endtimeMS = 0;}

                    }
                    catch (SQLException e) {e.printStackTrace(); endtimeMS = 0;}

                }
            });
        }
        else
            endtimeMS = 0;
    }


    private void startTimer(){
        long now = System.currentTimeMillis();
        Log.d(TAG, "startTimer: Entered Function");

        if (endtimeMS - now < 0){
            Log.d(TAG, "**OUT OF BOUNDS TIME. TIMER NOT SET**");
        }
        else {
            long timerLeft = Math.abs(endtimeMS - now);
            timer = new CountDownTimer(timerLeft, 1000) {
                @Override
                public void onTick(long l) {
                    long hrs = (l / 1000) / 3600;
                    long min = (l/ 1000) % 3600 / 60;
                    long sec = (l / 1000) % 60;
                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, min, sec);
//                    Log.d(TAG, "**" + timeFormatted + "**" );
                    countDownTimer.setText(timeFormatted);
                }

                @Override
                public void onFinish() {
                    countDownTimer.setText("00:00:00");
                }
            }.start();
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

