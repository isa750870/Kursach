package com.example.clinicapp.database;

import android.content.Context;
import android.content.SharedPreferences;

public class RegistrationHelper {

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_data"; // Имя файла настроек

    public RegistrationHelper(Context context) {
        // Использование статического имени файла
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserData(String name, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", name);
        editor.putString("user_email", email);
        editor.apply(); // Используем apply для асинхронного сохранения
    }
}