package com.example.parkwise;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.UUID;

public class BluetoothHelper {
    private static final String TAG = "BluetoothHelper";
    public static final int REQUEST_BLUETOOTH_CONNECT = 100;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic signalCharacteristic;

    private static final UUID SERVICE_UUID = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214");

    public BluetoothHelper(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean initialize() {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth is not supported on this device.");
            return false;
        }
        return true;
    }

    public void connectToDevice(String deviceAddress) {
        if (checkBluetoothPermissions()) {
            if (bluetoothAdapter == null || deviceAddress == null) return;
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            if (device == null) {
                Log.w(TAG, "Device not found. Unable to connect.");
                return;
            }
            bluetoothGatt = device.connectGatt(context, false, gattCallback);
        }
    }

    public boolean checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            }
            return false;
        }
        return true;
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(SERVICE_UUID);
                if (service != null) {
                    signalCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                    if (signalCharacteristic == null) {
                        Log.w(TAG, "Signal characteristic not found.");
                        return;
                    }
                    Log.i(TAG, "Signal characteristic found.");
                } else {
                    Log.w(TAG, "BLE service not found.");
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Characteristic write successful.");
            } else {
                Log.w(TAG, "Characteristic write unsuccessful, status: " + status);
            }
        }
    };

    public void sendSignal(short signal) {
        if (signalCharacteristic != null) {
            signalCharacteristic.setValue(new byte[]{(byte) (signal & 0xff), (byte) ((signal >> 8) & 0xff)});
            if (!bluetoothGatt.writeCharacteristic(signalCharacteristic)) {
                Log.w(TAG, "Failed to write characteristic");
            }
        } else {
            Log.w(TAG, "Characteristic is null");
        }
    }

    public void close() {
        if (bluetoothGatt == null) return;
        bluetoothGatt.close();
        bluetoothGatt = null;
    }
}
