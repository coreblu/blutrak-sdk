package com.averos.blutrak.sample;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by hassan on 5/20/2017.
 */

public class Utils {
    public static void toastShort(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
