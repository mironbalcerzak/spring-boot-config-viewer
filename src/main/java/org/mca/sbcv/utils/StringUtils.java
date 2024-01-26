package org.mca.sbcv.utils;

public final class StringUtils {

    private StringUtils() {
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        if (str == null || str.isBlank()) {
            return defaultStr;
        }
        return str;
    }
}
