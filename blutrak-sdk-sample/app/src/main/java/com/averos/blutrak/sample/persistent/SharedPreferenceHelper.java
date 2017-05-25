package com.averos.blutrak.sample.persistent;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceHelper {



	public static void putString(String key , String value , Context ctx){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key,value);
		editor.commit();
	}


	public static void putInteger(String key , Integer value , Context ctx){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key,value);
		editor.commit();
	}


	public static String getString(String key , String defaultValue , Context ctx){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return  pref.getString(key,defaultValue);
	}


	public static Integer getIntger(String key , String defaultValue , Context ctx){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		int token = pref.getInt(key,-1);
		return  token==-1?null:token;
	}
}
