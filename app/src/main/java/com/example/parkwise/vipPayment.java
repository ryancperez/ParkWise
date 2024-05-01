package com.example.parkwise;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link vipPayment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class vipPayment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SharedPreferences sharedPreferences;
    String username, deviceID;

    public vipPayment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment vipPayment.
     */
    // TODO: Rename and change types and number of parameters
    public static vipPayment newInstance(String param1, String param2) {
        vipPayment fragment = new vipPayment();
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

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "default_value");
        deviceID = sharedPreferences.getString("deviceID", "default_value");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vip_payment, container, false);
        TextView textView = view.findViewById(R.id.paymentText);
        ChipGroup chipGroup = view.findViewById(R.id.chip_Group);
        Button confirmButton = view.findViewById(R.id.CPbutton);
        Chip chip10=view.findViewById(R.id.chip10);
        Chip chip9=view.findViewById(R.id.chip9);
        Chip chip8=view.findViewById(R.id.chip8);
        Chip chip7=view.findViewById(R.id.chip7);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chip10.isChecked())
                {
                    performPayment("30");

                } else if (chip9.isChecked()) {

                    performPayment("60");

                } else if (chip8.isChecked()) {

                    performPayment("120");


                } else if (chip7.isChecked()) {
                    performPayment("1440");

                }

                Fragment fragment = new VIPFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

//                VIPFragment frag = new VIPFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.commit();


            }
        });
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if(checkedIds.size() > 0) {
                    confirmButton.setEnabled(true);

                }
                else
                    confirmButton.setEnabled(false);

            }});

        // Retrieve lotName from HomeFragment Info Window Click bundle argument
        Bundle args = getArguments();
        if (args != null) {
            String lotName = args.getString("lotName");
            if (lotName != null) {
                textView.setText(lotName);
            } else {
                // Handle the situation where lotName is null
                // For now, you can set a default value or display a message
                textView.setText("Lot name not provided");
            }
        } else {
            // Handle the situation where args is null
            // For now, you can set a default value or display a message
            textView.setText("ParkWise VIP");
        }
        // Inflate the layout for this fragment
        return view;




    }

    private void performPayment(String time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseConnector dbConnector = new DatabaseConnector();
                String TZ = "set time_zone = 'America/Los_Angeles';";
                String sql = "INSERT INTO time_table (start_datetime, end_datetime, username) " +
                        "VALUES (TIME(NOW()), TIME(DATE_ADD(NOW(), INTERVAL ? MINUTE)), ?)";
                String vip = "UPDATE USER SET vip = true WHERE username = ?;";
                String device = "UPDATE Device SET isAvailable = false WHERE deviceID = ?;";
                try (Connection conn = dbConnector.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql);
                     PreparedStatement setTZ = conn.prepareStatement(TZ);
                     PreparedStatement setVIP = conn.prepareStatement(vip);
                     PreparedStatement setDevice = conn.prepareStatement(device)) {
                    setTZ.executeUpdate();
                    pstmt.setString(1, time);
                    pstmt.setString(2, username);
                    pstmt.executeUpdate();
                    setVIP.setString(1, username);
                    setVIP.executeUpdate();
                    setDevice.setString(1,deviceID);
                    setDevice.executeUpdate();

                    showToastOnUiThread("Payment Successful!");

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isVIP", true);
                    editor.apply();

                }
                catch (SQLException e) {e.printStackTrace(); showToastOnUiThread("Payment Unsuccessful");}

            }
        }).start();
    }

    private void showToast(final String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void showToastOnUiThread(final String message) {
        getActivity().runOnUiThread(() -> showToast(message));
    }
}