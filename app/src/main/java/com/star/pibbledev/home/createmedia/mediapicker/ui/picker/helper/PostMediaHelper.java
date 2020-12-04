package com.star.pibbledev.home.createmedia.mediapicker.ui.picker.helper;

import android.text.TextUtils;

import java.util.ArrayList;

public class PostMediaHelper {
    public static final ArrayList<String> sCachePost = new ArrayList<>();

    public static ArrayList<String> concatenate(ArrayList<String> list1) {
        ArrayList<String> paths = new ArrayList<>(PostMediaHelper.sCachePost);

        for (String cachePath : list1) {
            if (TextUtils.isEmpty(cachePath)) {
                continue;
            }

            if (paths.contains(cachePath)) {
                continue;
            }

            paths.add(cachePath);
        }

        return paths;
    }
}
