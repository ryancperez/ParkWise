package com.example.parkwise;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VIPFragment extends Fragment {
    private BluetoothHelper bluetoothHelper;
    private ImageButton mainButton;
    private boolean lock = true;

    private String deviceID;
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
        deviceID = sharedPreferences.getString("device", "default_value");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_v_i_p, container, false);
        mainButton = view.findViewById(R.id.mainButton);
        mainButton.setOnClickListener(v -> {
            if (!lock) {
                bluetoothHelper.sendSignal((short) 1);
            } else {
                bluetoothHelper.sendSignal((short) 0);
            }
            lock = !lock;
            updateButtonImage();
        });

        showPopupWindow();
        return view;
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
}