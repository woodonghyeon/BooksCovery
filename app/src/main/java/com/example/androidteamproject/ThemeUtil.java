package com.example.androidteamproject;

import android.app.Presentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtil {
    public static final String LIGHT_MODE = "light";
    public static final String DARK_MODE = "dark";
    public static final String DEFAULT_MODE = "defalut";
    public static final String TAG = "ThemeUtil";
    private static Presentation activity;

    public static void applyTheme(String themeColor) {
        switch (themeColor) {
            case LIGHT_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // 라이트모드
                Log.d(TAG, "라이트 모드 적용되어 있음");
                break;

            case DARK_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // 다크 모드
                Log.d(TAG, "다크 모드 적용되어 있음");
                break;
        }
    }

    public static void modSave(Context context, String select_mod){
        SharedPreferences sp;
        sp = context.getSharedPreferences("mod", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mod",select_mod);
        editor.commit();
    }

    public static String modLoad(Context context){
        SharedPreferences sp;
        sp = context.getSharedPreferences("mod", Context.MODE_PRIVATE);
        String load_mod = sp.getString("mod","light");
        return load_mod;
    }
}
