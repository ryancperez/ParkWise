package com.example.parkwise;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class motorActivation extends AppCompatActivity {
    private static final String TAG = "BLEApp";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic signalCharacteristic;
    private boolean isServiceDiscovered = false;
    private final String DEVICE_ADDRESS = "F4:12:FA:6E:E7:59";
    private final static UUID CUSTOM_SERVICE_UUID = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214");
    private final static UUID CUSTOM_CHARACTERISTIC_UUID = UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214");

    public void connectAndSendSignal(short signal) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        checkPermissionsAndBluetooth();
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    private void checkPermissionsAndBluetooth() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE},
                    REQUEST_PERMISSIONS);
        } else {
            ensureBluetoothEnabled();
        }
    }

    private void ensureBluetoothEnabled() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToDevice();
        }
    }

    private void connectToDevice() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
    }

    private void sendSignalToBluetoothDevice(short signal) {
        if (bluetoothGatt != null && signalCharacteristic != null) {
            byte[] signalBytes = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(signal).array();
            signalCharacteristic.setValue(signalBytes);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothGatt.writeCharacteristic(signalCharacteristic);
        } else {
            Log.d(TAG, "Service not discovered or characteristic not found.");
            Toast.makeText(motorActivation.this, "Device not ready. Please wait.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ensureBluetoothEnabled();
        } else {
            showPermissionRationale();
        }
    }

    private void showPermissionRationale() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app requires location and Bluetooth permissions to function properly. Please grant the permissions.")
                .setPositiveButton("Grant Permissions", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothStateReceiver);
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    private void onBluetoothTurnedOff() {
        Toast.makeText(this, "Bluetooth turned off. Please turn it on to continue.", Toast.LENGTH_SHORT).show();
    }

    private void onBluetoothTurnedOn() {
        checkPermissionsAndBluetooth(); // Check permissions again and try to enable Bluetooth if it's not already on.
        connectToDevice();
    }

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        onBluetoothTurnedOff();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        onBluetoothTurnedOn();
                        break;
                }
            }
        }
    };

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server. Attempting to start service discovery:" + gatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
                isServiceDiscovered = false;
                if (bluetoothGatt != null) {
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                isServiceDiscovered = true;
                BluetoothGattService service = gatt.getService(CUSTOM_SERVICE_UUID);
                if (service != null) {
                    signalCharacteristic = service.getCharacteristic(CUSTOM_CHARACTERISTIC_UUID);
                    if (signalCharacteristic != null) {
                        Log.i(TAG, "Characteristic found. Ready for operations.");
                        // Send signal after service discovery
                        //short signal;
                    } else {
                        Log.e(TAG, "Characteristic not found.");
                    }
                } else {
                    Log.e(TAG, "Service not found.");
                }
            }
        }
    };
}
