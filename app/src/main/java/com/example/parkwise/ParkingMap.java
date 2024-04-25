package com.example.parkwise;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParkingMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkingMap extends Fragment {

    private static final String TAG = "ParkingMap";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton spot_one;
    private ImageButton spot_two;
    private String device1ID = "1";
    private String device2ID = "2";

    private String[] devices = {device1ID,device2ID};

    public ParkingMap() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParkingMap.
     */
    // TODO: Rename and change types and number of parameters
    public static ParkingMap newInstance(String param1, String param2) {
        ParkingMap fragment = new ParkingMap();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking_map, container, false);

        spot_one = view.findViewById(R.id.spot_one);
        spot_two = view.findViewById(R.id.spot_two);
        setAvailability();

        // Inflate the layout for this fragment
        return view;
    }

    private void setAvailability(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseConnector dbConnector = new DatabaseConnector();
                String sql = "SELECT isAvailable FROM ParkWise.Device WHERE deviceID = ?;";
                try (Connection conn = dbConnector.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql);) {

                        for (String device : devices) {
                            // Insert the new user data into the database

//                            PreparedStatement pstmt = conn.prepareStatement(sql);
                            pstmt.setString(1, device);
                            try(ResultSet resultSet = pstmt.executeQuery();){
                                if (resultSet.next()) {
                                    Log.d(TAG, "GOT AVAILABILITY");
                                    boolean isAvailable = resultSet.getBoolean("isAvailable");
                                    setButtonImage(isAvailable,device);
                                }
                                else{
                                    Log.d(TAG, "Device not found");
                                }
                            } catch (SQLException e) {e.printStackTrace();}

                        }


                }
                catch (SQLException e) {e.printStackTrace();}

            }
        }).start();
    }

    private void setButtonImage(boolean available, String id){
        if (available && id == device1ID){
            spot_one.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.parkwiseunlocked, null));
            spot_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new vipPayment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
        if (available && id == device2ID){
            spot_two.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.parkwiseunlocked, null));
            spot_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new vipPayment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    spot_two.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.parkwiselocked, null ));
                }
            });
        }

    }


}