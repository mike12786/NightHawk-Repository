package com.zapporoo.nighthawk.util;


import android.util.Log;

import com.zapporoo.nighthawk.App;

import java.util.List;

public class NhLog {

    public static void w(String TAG, String msg){
        if (msg == null)
            msg = "NULL";
        if (App.DEBUG)
            Log.w(TAG, msg);
    }

	public static void e(String TAG, String msg){
		if (msg == null)
			msg = "NULL";
		if (App.DEBUG)
			Log.e(TAG, msg);
	}

	public static void e(String TAG, String msg, Exception e) {
		if (msg == null)
			msg = "NULL";
		if (App.DEBUG)
			Log.e(TAG, msg, e);
	}

	public static void d(String TAG, String msg) {
		if (msg == null)
			msg = "NULL";
		if (App.DEBUG)
			Log.d(TAG, msg);
	}

	public static void i(String TAG, String msg) {
		if (msg == null)
			msg = "NULL";
		if (App.DEBUG)
			Log.i(TAG, msg);
	}

	public static void e(String tag, List<String> errors) {
		for (String s:errors){
			Log.e(tag, s + "\n");
		}
		
	}

	public static void breakpoint() {
		if (App.DEBUG)
			Log.e("Log", "break");
	}
}
