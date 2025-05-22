package com.example.clinicapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.clinicapp.R;
import com.example.clinicapp.activities.MainActivity;
import com.example.clinicapp.database.DatabaseHelper;
import com.example.clinicapp.models.User;

public class ProfileFragment extends BaseFragment {
    private TextView textViewName, textViewEmail, textViewPhone;
    private Button buttonLogout;
    private DatabaseHelper databaseHelper;

    private Button buttonMyTests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        databaseHelper = new DatabaseHelper(getActivity());

        textViewName = view.findViewById(R.id.textViewName);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewPhone = view.findViewById(R.id.textViewPhone);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        // Получаем ID текущего пользователя
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            // Загружаем данные пользователя
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                textViewName.setText(user.getFullName());
                textViewEmail.setText(user.getEmail());
                textViewPhone.setText(user.getPhone());
            }
        }

        Button buttonMyTests = view.findViewById(R.id.buttonMyTests);
        buttonMyTests.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new UserTestsFragment());
            }
        });
        Button btnAccountSettings = view.findViewById(R.id.btnAccountSettings);
        btnAccountSettings.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new AccountSettingsFragment());
        });

        buttonLogout.setOnClickListener(v -> {
            // Очищаем SharedPreferences при выходе
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("user_id");
            editor.apply();

            ((MainActivity) getActivity()).showBottomNavigation(false);
            ((MainActivity) getActivity()).loadFragment(new LoginFragment());
        });

        return view;
    }
}