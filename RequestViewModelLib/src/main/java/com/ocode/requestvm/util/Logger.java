package com.ocode.requestvm.util;

import android.util.Log;

public class Logger {
    public enum LogLevel {
        ERROR(0), INFO(1);
        private int value;

        LogLevel(int value) {
            this.value = value;
        }
    }

    private static LogLevel logLevel = LogLevel.ERROR;

    public static void setLogLevel(LogLevel logLevel) {
        Logger.logLevel = logLevel;
    }

    public static void logI(String msg) {
        if (logLevel.value >= LogLevel.INFO.value)
            Log.i("[RequestViewModel]", msg);
    }

    public static void logE(String msg) {
        if (logLevel.value >= LogLevel.ERROR.value)
            Log.e("[RequestViewModel] ", msg);
    }
}
