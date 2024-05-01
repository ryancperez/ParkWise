package com.example.parkwise;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VIPFragment extends Fragment {
    private BluetoothHelper bluetoothHelper;
    private ImageButton mainButton;
    private Button cancelButton;
    private boolean lock = true;

    private String deviceID, username, deviceSQLID;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            bluetoothHelper = new BluetoothHelper(getActivity());
            bluetoothHelper.initialize();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "default_value");
        deviceID = sharedPreferences.getString("device", "default_value");
        deviceSQLID = sharedPreferences.getString("deviceID", "default_value");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_v_i_p, container, false);
        mainButton = view.findViewById(R.id.mainButton);
        cancelButton = view.findViewById(R.id.cancel_button);
        mainButton.setOnClickListener(v -> {
            if (!lock) {
                bluetoothHelper.sendSignal((short) 1);
            } else {
                bluetoothHelper.sendSignal((short) 0);
            }
            lock = !lock;
            updateButtonImage();
        });

        cancelButton.setOnClickListener( v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isVIP",false);
            editor.putString("device", "default_value");
            editor.apply();
            removeVIP();
        });

        showPopupWindow();
        return view;
    }

    private void removeVIP() {
        Handler handler = new Handler();
        handler.post(() -> {
            DatabaseConnector dbConnector = new DatabaseConnector();
            String TZ = "set time_zone = 'America/Los_Angeles';";
            String vip = "UPDATE USER SET vip = false WHERE username = ?;";
            String sql = "UPDATE time_table SET end_datetime = TIME(NOW()) WHERE username = ?;";
            String device = "UPDATE Device SET isAvailable = true WHERE deviceID = ?;";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 PreparedStatement setVIP = conn.prepareStatement(vip);
                 PreparedStatement setTZ = conn.prepareStatement(TZ);
                 PreparedStatement setDevice = conn.prepareStatement(device)) {
                setTZ.executeUpdate();
                setVIP.setString(1,username);
                setVIP.executeUpdate();
                pstmt.setString(1, username);
                pstmt.executeUpdate();
                setDevice.setString(1,deviceSQLID);
                setDevice.executeUpdate();

                showToastOnUiThread("Successful!");

            }
            catch (SQLException e) {e.printStackTrace(); showToastOnUiThread("Unsuccessful. Try Again.");}
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Connect to the device when the view is created and visible
        //bluetoothHelper.connectToDevice("F4:12:FA:6E:E7:59");
        bluetoothHelper.connectToDevice(deviceID);
    }

    private void updateButtonImage() {
        int drawableId = lock ? R.drawable.parkwiselocked : R.drawable.parkwiseunlocked;
        mainButton.setImageDrawable(getResources().getDrawable(drawableId, getActivity().getTheme()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BluetoothHelper.REQUEST_BLUETOOTH_CONNECT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. You can retry connecting or proceed with your logic here.
            } else {
                // Permission denied. Handle the feature or inform the user accordingly.
            }
        }
    }

    private void showPopupWindow() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_message); // Assuming you have a layout file for your popup

        // Set up dialog content and buttons (e.g., using findViewById)
        TextView messageText = dialog.findViewById(R.id.message);
        messageText.setText("Must be in range to access stall!");

        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothHelper.close();
    }

    private void showToastOnUiThread(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(message);
            }
        });
    }

    private void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}