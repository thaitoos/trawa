package com.example.trawa01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ViewModel.ActivityViewModel;
import model.ActivityEntity;
import model.ActivityType;

public class BluetoothActivity extends AppCompatActivity {
    private BluetoothData localData;
    private BluetoothData receivedData;
    private ConnectedThread connectedThread;
    private boolean goesFirst = false;
    private static final int REQUEST_BLUETOOTH = 1;
    private static final int REQUEST__NEW_BLUETOOTH = 2;
    private static final int REQUEST_ENABLE_BLUETOOTH = 3;
    private static final int REQUEST_LOCATION = 4;
    private static final String APP_NAME = "Trawa";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private static final int MESSAGE_READ = 6;
    private Handler handler;

    private ListView pairingListView;
    private BluetoothAdapter bluetoothAdapter;
    private final List<BluetoothDevice> deviceList = new ArrayList<>();
    private ArrayAdapter<String> deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        pairingListView = findViewById(R.id.pairingListView);
        /*if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_BLUETOOTH);
        }*/

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
            }, REQUEST__NEW_BLUETOOTH);
        }

        // ask for fine location permission
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        }

        initializeData();

        initializeBluetooth();

        // Initialize the list view and adapter
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        pairingListView.setAdapter(deviceListAdapter);

        // make this device discoverable
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        pairingListView.setOnItemClickListener((parent, view, position, id) -> {
            // Handle device selection
            BluetoothDevice device = deviceList.get(position);
            // Connect to the selected device
            connectToDevice(device);
        });

        handler = new Handler(message -> {
            if (message.what == MESSAGE_READ) {
                byte[] readBuf = (byte[]) message.obj;
                String readMessage = new String(readBuf, 0, message.arg1);
                Toast.makeText(this, "Received: " + readMessage, Toast.LENGTH_SHORT).show();

                receivedData = new BluetoothData(readMessage);

                if(!goesFirst) {
                    connectedThread.write(localData.toString().getBytes());
                }
            }
            return true;
        });

        startDeviceDiscovery();
        startServer();
    }

    @SuppressLint("MissingPermission")
    private void initializeBluetooth() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter != null) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
                }
            } else {
                // Handle the case where the device doesn't support Bluetooth
                showError("Bluetooth is not supported on this device.");
            }
        } else {
            // Handle the case where BluetoothManager is not available
            showError("BluetoothManager is not available.");
        }
    }

    private void showError(String message) {
        // Implement a method to show the error to the user, such as a Toast or a dialog
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH || requestCode == REQUEST__NEW_BLUETOOTH) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                initializeBluetooth();
            } else {
                showError("Bluetooth permissions are required to use this feature.");
            }
        }
        if(requestCode == REQUEST_LOCATION) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (!allPermissionsGranted) {
                showError("Location permissions are required to use this feature.");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startDeviceDiscovery() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        System.out.println( bluetoothAdapter.startDiscovery() );
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null) {
                    deviceList.add(device);
                    deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
                    deviceListAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (deviceListAdapter.getCount() == 0) {
                    showError("No devices found");
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        new Thread(() -> {
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                runOnUiThread(() -> showError("Connected to " + device.getName()));
                unregisterReceiver(receiver);
                connectedThread = new ConnectedThread(socket, handler);
                connectedThread.start();
                //connectedThread.write("Hello".getBytes());
                // Now you can manage the connection in a separate thread
            } catch (IOException e) {
                runOnUiThread(() -> showError("Failed to connect to " + device.getName()));
            }
        }).start();
    }

    @SuppressLint("MissingPermission")
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
                while (true) {
                    socket = serverSocket.accept();
                    if (socket != null) {
                        runOnUiThread(() -> showError("A device connected"));
                        // Handle the connection in a separate thread
                        serverSocket.close();
                        unregisterReceiver(receiver);
                        connectedThread = new ConnectedThread(socket, handler);
                        connectedThread.start();
                        goesFirst = true;
                        connectedThread.write(localData.toString().getBytes());
                        break;
                    }
                }
            } catch (IOException e) {
                runOnUiThread(() -> showError("Error starting server: " + e.getMessage()));
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initializeData() {
        ActivityViewModel activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        List<ActivityEntity> activities = activityViewModel.getAllActivitiesList();
        localData = new BluetoothData(0, 0, 0, 0);
        for(ActivityEntity activity : activities) {
            if(activity.getType().equals(ActivityType.CYCLING)){
                localData.setDistanceCycling(localData.getDistanceCycling() + (int)activity.getDistance());
            } else if(activity.getType().equals(ActivityType.RUNNING)){
                localData.setDistanceRunning(localData.getDistanceRunning() + (int)activity.getDistance());
            } else if(activity.getType().equals(ActivityType.WALKING)){
                localData.setDistanceWalking(localData.getDistanceWalking() + (int)activity.getDistance());
            } else if(activity.getType().equals(ActivityType.STATIONARY)){
                localData.setTimeStationary(localData.getTimeStationary() + (int)activity.getDuration());
            }
        }
    }
}
