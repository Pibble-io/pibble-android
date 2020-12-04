package com.star.pibbledev.services.global.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavePref {

    String TAG = "SavePref";
    Context ctx;
    private SharedPreferences prefs;

    public SavePref(Context ctx) {
        this.ctx = ctx;
        prefs = this.ctx.getSharedPreferences("myPref", this.ctx.MODE_PRIVATE );
    }

    public void saveBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveFloat(String key, double value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, (float) value);
        editor.commit();
    }

    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void removeAll() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
}
