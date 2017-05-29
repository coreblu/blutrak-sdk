package com.averos.blutrak.sample;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.averos.blutrak.blutraksdk.Bluetooth.BlutrakDevice;
import com.averos.blutrak.blutraksdk.Bluetooth.BlutrakOperationCompleteListener;
import com.averos.blutrak.blutraksdk.Bluetooth.ConnectionState;
import com.averos.blutrak.blutraksdk.Bluetooth.ConnectionStateChangeListener;
import com.averos.blutrak.blutraksdk.Bluetooth.KeyPressListener;
import com.averos.blutrak.blutraksdk.DeviceManager;
import com.averos.blutrak.sample.List.ListModel;

public class SecondActivity extends AppCompatActivity {
    public final  String TAG = getClass().getSimpleName();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.LogD(TAG, "onDestroy");
        device.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.LogD(TAG, "onStop");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.LogD(TAG, "onStart");

    }

    BlutrakDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        device = DeviceManager.createDevice("A0:E6:F8:3F:41:84", "new", this);
       // device =new BlutrakDeviceImpl(getApplicationContext(),"A0:E6:F8:3F:41:84", "new");

        device.addKeyPressListener(keyPressListener);
        device.addConnectionStateChangeListener(new ConnectionStateChangeListener() {
            @Override
            public void onConnectionStateChange(BlutrakDevice device, ConnectionState connectionState) {
                Logger.LogD(TAG, "state=" + device.getConnectionState()+" | MAC = "+device.getDeviceAddress());

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                device.stopRinging(new BlutrakOperationCompleteListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(int error) {

                    }
                });
            }
        },3*1000);
//
       device.connect(true);
    }


    private final KeyPressListener keyPressListener = new KeyPressListener() {
        @Override
        public void onShortPress(BlutrakDevice device) {
            Logger.LogD(TAG, "onShortPress from " + device.getDeviceAlias());
        }

        @Override
        public void onLongPress(BlutrakDevice device) {
            Logger.LogD(TAG, "onLongPress from " + device.getDeviceAlias());
        }
    };
}
