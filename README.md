**Step 1:**
Press button on blutrak device and find blutrak device
        
    new DeviceManager().findDevice(new DeviceManager.DeviceListener() {
            @Override
            public void onDeviceFound(String mac) {
            //device found save its mac to connect
            }

            @Override
            public void onError(int error) {
              switch (error) {
                    case DeviceManager.ERROR_PAIRING_MODE_NOT_ENABLED:
                        break;
                    case DeviceManager.ERROR_DEVICE_READ_FAILED:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTimeout() {
              //no device found
            }
        }, this, 10);

**Step 2:**
Connect to blutrak device from mac

     BlutrakDevice btDevice = DeviceManager.createDevice(mac, alias, 
     getApplicationContext());
     btDevice.connect(true);
     device.addConnectionStateChangeListener(new ConnectionStateChangeListener() {
            @Override
            public void onConnectionStateChange(BlutrakDevice blutrakDevice, ConnectionState connectionState) {
                switch (connectionState) {
                    case Connected:
                        break;
                    case Disconnected:
                        break;
                    case Connecting:
                        break;
                    case Disconnecting:
                        break;
                }
            }
        });
        device.addKeyPressListener(new KeyPressListener() {
            @Override
            public void onShortPress(BlutrakDevice blutrakDevice) {
                
            }

            @Override
            public void onLongPress(BlutrakDevice blutrakDevice) {

            }
        });

**Step 3:**
Ring Tag

    if (device.isConnected()) {
            device.ring(new BlutrakOperationCompleteListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFail(int error) {
                   //handle error
                }
            });
        } 
