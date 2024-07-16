package com.example.androidteamproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "UserSession";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_MEMBER_ID = "MemberId";
    private static final String KEY_NAME = "Name";
    private static String KEY_GENDER = "Gender";
    private static String KEY_AGE = "Age";
    private static String KEY_DEPARTMENT_ID = "DepartmentId";
    private static final String KEY_ID = "Id";
    private static String KEY_EMAIL = "Email";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(int member_id, String name, int department_id, String id) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_MEMBER_ID, member_id);
        editor.putString(KEY_NAME, name);
        editor.putInt(KEY_DEPARTMENT_ID, department_id);
        editor.putString(KEY_ID, id);
        editor.commit();
    }

    public void settingSession(int member_id, String name, String gender, int age, int departmentId, String email) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_MEMBER_ID, member_id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_GENDER, gender);
        editor.putInt(KEY_AGE, age);
        editor.putInt(KEY_DEPARTMENT_ID, departmentId);
        editor.putString(KEY_EMAIL, email);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public int getMemberId() {
        return pref.getInt(KEY_MEMBER_ID, 0);
    }

    public String getName() {
        return pref.getString(KEY_NAME, null);
    }

    public int getDepartmentId() {
        return pref.getInt(KEY_DEPARTMENT_ID, 0);
    }

    public String getId() {
        return pref.getString(KEY_ID, null);
    }

    public String getAge() {
        return pref.getString(KEY_AGE, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getGender() {
        return pref.getString(KEY_GENDER, null);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}
