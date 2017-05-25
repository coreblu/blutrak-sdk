package com.averos.blutrak.sample;

import android.util.Log;

/**
 * Created by hassan on 5/14/2017.
 */

public class Logger {
    public static boolean enable = true;

    public static void LogD(String tag, String msg) {
        if (enable)
            Log.d(tag, msg);
    }
}
