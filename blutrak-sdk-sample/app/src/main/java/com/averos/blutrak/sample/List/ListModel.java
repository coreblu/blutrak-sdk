package com.averos.blutrak.sample.List;

import com.averos.blutrak.blutraksdk.Bluetooth.ConnectionState;

/**
 * Created by hassan on 5/21/2017.
 */

public class ListModel {
    private ConnectionState connectionState=ConnectionState.Disconnected;
    private String name,mac;
    private boolean pendingAlert = false;


    public ListModel(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isPendingAlert() {
        return pendingAlert;
    }

    public void setPendingAlert(boolean pendingAlert) {
        this.pendingAlert = pendingAlert;
    }
}
