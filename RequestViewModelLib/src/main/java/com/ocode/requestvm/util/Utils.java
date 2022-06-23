package com.ocode.requestvm.util;

/**
 * @author:eric
 * @date:6/6/22
 */
public class Utils {
    public static <T> T checkNotNull(T object, String msg) {
        if (object == null) {
            throw new NullPointerException(msg);
        }
        return object;
    }
}
