package com.star.pibbledev.services.global.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

    public class ReadPref {
        boolean result = false;
        String TAG = "ReadPref";
        Context ctx;
        String res = "";
        private SharedPreferences prefs;

        public ReadPref(Context ctx) {
            this.ctx = ctx;
            prefs = this.ctx.getSharedPreferences("myPref", this.ctx.MODE_PRIVATE );
        }

        public boolean getBooleanValue(String key) {
            return prefs.getBoolean(key, false);
        }

        public boolean getBooleanValueTrue(String key) {
            return prefs.getBoolean(key, true);
        }

        public String getStringValue(String key) {
            return prefs.getString(key, "");
        }

        public float getDoubleValue(String key) {
            return prefs.getFloat(key, 0);
        }

        public int getIntValue(String key) {
            return prefs.getInt(key, -1);
        }

        public boolean keyExist(String key) {
            return prefs.contains(key);
        }
    }
