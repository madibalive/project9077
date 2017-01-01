package com.venu.venutheta.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Madiba on 12/28/2016.
 */

public class PreferenceHelper {
    private static final String PREF_NAME = "io.t28.example.user";
    private static final String PREF_USER_NAME = "user_age";
    private static final String USER_NAME = "user_age";
    private static final String DEFAULT_USER_NAME = "guest";
    private static final String PREF_USER_AGE = "";
    private static final int DEFAULT_USER_AGE = 20;

    public static boolean hasName(Context context) {
        return getSharedPreferences(context).contains(PREF_USER_NAME);
    }

    public static String getName(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_NAME, DEFAULT_USER_NAME);
    }

    public static void putName(Context context, String value) {
        getSharedPreferences(context).edit().putString(PREF_USER_NAME, value).apply();
    }

    public static void removeName(Context context) {
         getSharedPreferences(context).edit().remove(USER_NAME).apply();
    }

    public static boolean hasAge(Context context) {
        return getSharedPreferences(context).contains(PREF_USER_AGE);
    }

    public static int getAge(Context context) {
        return getSharedPreferences(context).getInt(PREF_USER_AGE, DEFAULT_USER_AGE);
    }

    public static void putAge(Context context, int value) {
         getSharedPreferences(context).edit().putInt(PREF_USER_AGE, value).apply();
    }

    public static void removeAge(Context context) {
         getSharedPreferences(context).edit().remove(PREF_USER_AGE).apply();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}