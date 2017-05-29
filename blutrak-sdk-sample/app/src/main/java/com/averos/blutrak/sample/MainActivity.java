package com.averos.blutrak.sample;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.averos.blutrak.blutraksdk.Bluetooth.BlutrakDevice;
import com.averos.blutrak.blutraksdk.DeviceManager;
import com.averos.blutrak.blutraksdk.Model.BlutrakDeviceInfo;
import com.averos.blutrak.blutraksdk.Bluetooth.BlutrakOperationCompleteListener;
import com.averos.blutrak.blutraksdk.Bluetooth.ConnectionState;
import com.averos.blutrak.blutraksdk.Bluetooth.ConnectionStateChangeListener;
import com.averos.blutrak.blutraksdk.Bluetooth.DeviceInformationReadListener;
import com.averos.blutrak.blutraksdk.Bluetooth.KeyPressListener;
import com.averos.blutrak.sample.List.ListAdapter;
import com.averos.blutrak.sample.List.ListModel;
import com.averos.blutrak.sample.persistent.SavedDevices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListAdapter.ListInteractionListener {
    private String TAG = getClass().getSimpleName();
    private Map<String, BlutrakDevice> devices = new HashMap<>();
    private RecyclerView list;
    private ImageView addNewTag;
    private ListAdapter listAdapter;
    private Map<String, ListModel> listData = new HashMap<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.LogD(TAG, "onDestroy");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.LogD(TAG, "onStop");

    }

    private final ConnectionStateChangeListener connectionStateChangeListener = new ConnectionStateChangeListener() {
        @Override
        public void onConnectionStateChange(BlutrakDevice device, ConnectionState connectionState) {
            final ListModel model = listData.get(device.getDeviceAddress());
            if (model == null)
                return;
            model.setConnectionState(connectionState);
            updateList();
            switch (connectionState) {
                case Connected:
                    Logger.LogD(TAG, device.getDeviceAlias() + " | Connected");
                    break;
                case Disconnected:
                    Logger.LogD(TAG, device.getDeviceAlias() + " | Disconnected");
                    break;
                case Connecting:
                    Logger.LogD(TAG, device.getDeviceAlias() + " | Connecting");
                    break;
                case Disconnecting:
                    Logger.LogD(TAG, device.getDeviceAlias() + " | Disconnecting");
                    break;


            }
        }
    };

    private final KeyPressListener keyPressListener = new KeyPressListener() {
        @Override
        public void onShortPress(BlutrakDevice device) {
            Logger.LogD(TAG, "onShortPress from " + device.getDeviceAlias());
            final ListModel model = listData.get(device.getDeviceAddress());
            if (model == null)
                return;
            model.setPendingAlert(true);
            updateList();
        }

        @Override
        public void onLongPress(BlutrakDevice device) {
            Logger.LogD(TAG, "onLongPress from " + device.getDeviceAlias());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (ensureBleExists() && !isBleEnabled()) {
            enableBle();
        } else {
            connectAllTags();
            setUpList();
        }
    }

    private void initView() {
        list = (RecyclerView) findViewById(R.id.list);
        list.setOnClickListener(this);
        addNewTag = (ImageView) findViewById(R.id.add_new_tag);
        addNewTag.setOnClickListener(this);
    }

    private void setUpList() {
        addListItems();
        listAdapter = new ListAdapter(new ArrayList<>(listData.values()));
        list.setAdapter(listAdapter);
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listAdapter.setListInteractionListener(this);
    }

    private void addListItems() {
        for (BlutrakDevice device : devices.values()) {
            ListModel model = new ListModel(device.getDeviceAlias(), device.getDeviceAddress());
            model.setConnectionState(device.getConnectionState());
            listData.put(model.getMac(), model);
        }
    }

    private void updateList() {
        listAdapter.update(new ArrayList<>(listData.values()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_tag:
                showScanHint();
                break;
        }
    }

    private void connectAllTags() {
        Map<String, SavedDevices.SavedDevice> deviceMap = SavedDevices.get(getApplicationContext());
        for (SavedDevices.SavedDevice device : deviceMap.values()) {
            final BlutrakDevice btDevice = DeviceManager.createDevice(device.getMac(), device.getAlias(), getApplicationContext());
            btDevice.addKeyPressListener(keyPressListener);
            btDevice.addConnectionStateChangeListener(connectionStateChangeListener);
            btDevice.connect(true);
            devices.put(btDevice.getDeviceAddress(), btDevice);
        }
    }

    private void addTag(String mac, String alias) {
        if (devices.get(mac) != null) {
            Utils.toastShort(getApplicationContext(), "Device Already exists");
            return;
        }
        SavedDevices.add(mac, alias, getApplicationContext());
        BlutrakDevice device = DeviceManager.createDevice(mac, alias, getApplicationContext());
        device.connect(true);
        device.addConnectionStateChangeListener(connectionStateChangeListener);
        device.addKeyPressListener(keyPressListener);
        devices.put(device.getDeviceAddress(), device);
        ListModel model = new ListModel(device.getDeviceAlias(), device.getDeviceAddress());
        model.setConnectionState(device.getConnectionState());
        listData.put(model.getMac(), model);
        updateList();
    }

    private void showEnterTagNameDialog(final String mac) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Tag Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(input.getText())) {
                    addTag(mac, input.getText().toString());
                } else {
                    showEnterTagNameDialog(mac);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showScanHint() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Find tag");
        builder.setMessage("1) Press button on tag until you hear a beep sound" +
                "\n\n2) Place your tag very close to mobile phone");
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scanForNewTag();
            }
        });

        builder.create().show();
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.create().show();
    }

    private void showDeleteDialog(final String mac, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Tag");
        builder.setMessage("Are you sure you want to delete " + name);
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BlutrakDevice device = devices.remove(mac);
                listData.remove(mac);
                SavedDevices.remove(mac, getApplicationContext());
                device.disconnect();
                updateList();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.create().show();
    }

    private void scanForNewTag() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Scanning... please press button on tag until you hear a beep sound");
        dialog.setCancelable(false);
        dialog.show();
        new DeviceManager().findDevice(new DeviceManager.DeviceListener() {
            @Override
            public void onDeviceFound(String mac) {
                dialog.dismiss();
                showEnterTagNameDialog(mac);
            }

            @Override
            public void onError(int error) {
                dialog.dismiss();

                switch (error) {
                    case DeviceManager.ERROR_PAIRING_MODE_NOT_ENABLED:
                        Utils.toastLong(getApplicationContext(), "PLease press button on device until you hear a beep sound");
                        break;
                    case DeviceManager.ERROR_DEVICE_READ_FAILED:
                        Utils.toastLong(getApplicationContext(), "An error occur while communicating with device please try again");
                        break;
                    default:
                        Utils.toastLong(getApplicationContext(), "Unknown error occur, please try again");
                        break;
                }

            }

            @Override
            public void onTimeout() {
                dialog.dismiss();

                Utils.toastShort(getApplicationContext(), "No device found");
            }
        }, this, 10);
    }

    @Override
    public void deleteTag(String mac, String name) {
        showDeleteDialog(mac, name);
    }

    @Override
    public void startRinging(String mac, String name) {
        BlutrakDevice device = devices.get(mac);
        if (device.isConnected()) {
            device.ring(new BlutrakOperationCompleteListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(int error) {
                    Utils.toastShort(getApplicationContext(), "Failed to ring tag");
                }
            });
        } else {
            Utils.toastShort(getApplicationContext(), "Device not connected");
        }
    }

    @Override
    public void stopRinging(String mac, String name) {
        BlutrakDevice device = devices.get(mac);
        if (device.isConnected()) {
            device.stopRinging(new BlutrakOperationCompleteListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFail(int error) {
                    Utils.toastShort(getApplicationContext(), "Failed to stop ring");
                }
            });
        } else {
            Utils.toastShort(getApplicationContext(), "Device not connected");
        }
    }

    @Override
    public void sleepTag(String mac, String name) {
        BlutrakDevice device = devices.get(mac);
        if (device.isConnected()) {
            device.sleep(new BlutrakOperationCompleteListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFail(int error) {
                    Utils.toastShort(getApplicationContext(), "Failed to sleep tag");
                }
            });
        } else {
            Utils.toastShort(getApplicationContext(), "Device not connected");
        }
    }

    @Override
    public void showInfo(final String mac, final String name) {
        BlutrakDevice device = devices.get(mac);
        if (!device.isConnected()) {
            Utils.toastShort(getApplicationContext(), "Device not connected");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Getting device info");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            device.readDeviceInfo(new DeviceInformationReadListener() {
                @Override
                public void onDeviceInformationReady(BlutrakDeviceInfo blutrakDeviceInfo) {
                    progressDialog.dismiss();
                    showAlert(name + " Device Info", "Firmware: " + blutrakDeviceInfo.getFirmwareVersion()
                            + "\nBattery: " + blutrakDeviceInfo.getBatteryPercentage() + "%");
                }

                @Override
                public void onReadFailed(int error) {
                    progressDialog.dismiss();
                    showDeviceIOError("readDeviceInfo:" + name, error);
                }
            });
        }

    }

    private void showDeviceIOError(String tag, int error) {
        switch (error) {
            case BlutrakDevice.ERROR_DEVICE_NOT_CONNECTED:
                Utils.toastLong(getApplicationContext(), tag + " | Device is not connected");
                break;
            case BlutrakDevice.ERROR_RECEIVE_FAIL:
                Utils.toastLong(getApplicationContext(), tag + " | An error occur while communicating with device please try again");
                break;
            case BlutrakDevice.ERROR_SEND_COMMAND_FAIL:
                Utils.toastLong(getApplicationContext(), tag + " | An error occur while communicating with device please try again");
                break;
            default:
                Utils.toastLong(getApplicationContext(), tag + " | Unknown error occur, please try again");
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    showScanHint();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode != RESULT_OK) {
                    finish();
                }else{
                    connectAllTags();
                    setUpList();
                }
                break;
        }
    }

    /**
     * Tries to start Bluetooth adapter.
     */
    private void enableBle() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, 0);
    }

    /**
     * Checks whether the Bluetooth adapter is enabled.
     */
    private boolean isBleEnabled() {
        final BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        final BluetoothAdapter ba = bm.getAdapter();
        return ba != null && ba.isEnabled();
    }

    /**
     * Checks whether the device supports Bluetooth Low Energy communication
     *
     * @return <code>true</code> if BLE is supported, <code>false</code> otherwise
     */
    private boolean ensureBleExists() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Unsupported Device..", Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        return true;
    }

}
