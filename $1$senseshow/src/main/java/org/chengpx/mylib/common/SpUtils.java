package org.chengpx.mylib.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * create at 2018/4/29 11:27 by chengpx
 */
public class SpUtils {

    private static SpUtils sSpUtils;

    private final SharedPreferences mSharedPreferences;

    private SpUtils(Context context) {
        mSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public static SpUtils getInstance(Context context) {
        if (sSpUtils == null) {
            synchronized (SpUtils.class) {
                if (sSpUtils == null) {
                    sSpUtils = new SpUtils(context);
                }
            }
        }
        return sSpUtils;
    }

    public void putInt(String key, int val) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public void putBoolean(String key, boolean val) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return mSharedPreferences.getBoolean(key, defaultVal);
    }

    public int getInt(String key, int defaultVal) {
        return mSharedPreferences.getInt(key, defaultVal);
    }
}
