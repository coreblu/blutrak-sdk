package com.averos.blutrak.sample.persistent;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hassan on 5/20/2017.
 */

public class SavedDevices {
    private static String key = SavedDevices.class.getClass().getSimpleName();


    public static synchronized void add(String mac,String alias, Context context) {
        Map<String, SavedDevice> devices = get(context);
        devices.put(mac, new SavedDevice(mac,alias));
        SharedPreferenceHelper.putString(key, new Gson().toJson(devices), context);
    }

    public static synchronized void remove(String mac, Context context) {
        Map<String, SavedDevice> devices = get(context);
        devices.remove(mac);
        SharedPreferenceHelper.putString(key, new Gson().toJson(devices), context);
    }

    public static synchronized Map<String, SavedDevice> get(Context context) {
        String json = SharedPreferenceHelper.getString(key, null, context);
        if (json == null)
            return new HashMap<>();

        Type type = new TypeToken<Map<String, SavedDevice>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public static class SavedDevice {
        private String mac, alias;

        public SavedDevice(String mac, String alias) {
            this.mac = mac;
            this.alias = alias;
        }

        public String getMac() {
            return mac;
        }

        public String getAlias() {
            return alias;
        }
    }
}
