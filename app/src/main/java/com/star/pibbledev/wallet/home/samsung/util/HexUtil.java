package com.star.pibbledev.wallet.home.samsung.util;

import android.util.Log;

import java.util.Locale;

public class HexUtil {
    public static String toHexString(byte[] input, int offset, int length, boolean withPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (withPrefix) {
            stringBuilder.append("0x");
        }
        for (int i = offset; i < offset + length; i++) {
            stringBuilder.append(String.format("%02x", input[i] & 0xFF));
        }

        return stringBuilder.toString();
    }

    public static String toHexString(byte[] input) {
        return toHexString(input, 0, input.length, true);
    }


    public static String toHexString(byte[] input, boolean withPrefix) {
        return toHexString(input, 0, input.length, withPrefix);
    }

    public static byte[] toBytes(String str) {
        String s;
        if (str.length() > 2 && str.startsWith("0x")) {
            s = str.substring(2);
        } else {
            s = str;
        }

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void printHex(String tag, byte[] input) {
        String str = toHexString(input, false);
        final int hexLineLength = 128;
        int idx = 0;
        Log.v(tag, "last idx expected : " + str.length()/hexLineLength );
        for (int i = 0; i < str.length(); i += hexLineLength) {
            int endIdx = i+ hexLineLength;
            if(endIdx > str.length()){
                endIdx = str.length();
            }

            Log.v(tag,String.format(Locale.US, "idx : %05d ", idx) +  str.substring(i, endIdx));
            idx++;
        }
        Log.v(tag, "last idx : " + idx);
    }
}