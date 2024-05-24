package com.example.androidteamproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_NAME = "Name";
    private static final String KEY_GENDER = "Gender";
    private static final String KEY_AGE = "Age";
    private static final String KEY_DEPARTMENT_ID = "Department_id";
    private static final String KEY_ID = "Id";
    private static final String KEY_PASSWORD_KEY = "Password_Key";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_MODE = "Mode";
    private static final String KEY_UPDATE_DATE = "UpdateDate";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String gender, int age, int departmentId, String id, String password_key, String email, String mode, String updateDate) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_GENDER, gender);
        editor.putInt(KEY_AGE, age);
        editor.putInt(KEY_DEPARTMENT_ID, departmentId);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_PASSWORD_KEY, password_key);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MODE, mode);
        editor.putString(KEY_UPDATE_DATE, updateDate);
        editor.commit();
    }

    public String getName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getGender() {
        return pref.getString(KEY_GENDER, null);
    }

    public int getAge() {
        return pref.getInt(KEY_AGE, 0);
    }

    public int getDepartmentId() {
        return pref.getInt(KEY_DEPARTMENT_ID, 0);
    }

    public String getId() {
        return pref.getString(KEY_ID, null);
    }

    public String getPasswordKey() {
        return pref.getString(KEY_PASSWORD_KEY, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getMode() {
        return pref.getString(KEY_MODE, null);
    }

    public String getUpdateDate() {
        return pref.getString(KEY_UPDATE_DATE, null);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}
