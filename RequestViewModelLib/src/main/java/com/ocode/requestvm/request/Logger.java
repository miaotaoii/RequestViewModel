package com.ocode.requestvm.request;

import android.util.Log;

public class Logger {
    public static void log(String msg) {
        Log.i("[RequestViewModel]", msg);
    }

    public static void logE(String msg) {
        Log.e("[RequestViewModel] ", msg);
    }
}
