package com.ccst.kuaipan.tool;

import android.util.Log;

public class LogHelper {
    public static String TAG = "kuaipan";
    public static boolean isDebug = true;
    
    public static void log(String s){
        if(isDebug){
            Log.v(TAG, s);
        }
    }
}
