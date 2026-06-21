package com.example.endemik;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private static final String PREFS = "endemik_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    public static void applySavedTheme(Context context) {
        boolean darkMode = getPrefs(context).getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static boolean toggleTheme(Context context) {
        SharedPreferences prefs = getPrefs(context);
        boolean next = !prefs.getBoolean(KEY_DARK_MODE, false);
        prefs.edit().putBoolean(KEY_DARK_MODE, next).apply();
        AppCompatDelegate.setDefaultNightMode(
                next ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        return next;
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
