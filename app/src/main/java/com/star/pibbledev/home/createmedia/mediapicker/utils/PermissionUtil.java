package com.star.pibbledev.home.createmedia.mediapicker.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;

/**
 * Created by hieuvm on 11/12/16.
 * *
 */

public class PermissionUtil {
    private PermissionUtil() {
        // private constructor for utils class
    }

    public static boolean verifyPermission(int[] grantResult) {

        if (grantResult.length < 1) {
            return false;
        }

        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkAndroidMVersion() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static int[] getSelfPermission(Context context, String[] permission) {
        int[] selfPermission = new int[permission.length];
        for (int i = 0; i < permission.length; i++) {
            selfPermission[i] = ContextCompat.checkSelfPermission(context, permission[i]);
        }

        return selfPermission;
    }
}