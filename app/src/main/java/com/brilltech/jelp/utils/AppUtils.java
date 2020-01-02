package com.brilltech.jelp.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtils {
    public static void showLog(String tag, String msg) {
        if (tag != null && msg != null)
            Log.d(tag, "APP_FLOW --> " + msg);
    }

    public static String getDate(long time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("E, d MMM yyyy");
            return sdf.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDateSimple(long time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            return sdf.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}