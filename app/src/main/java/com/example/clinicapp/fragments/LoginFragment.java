package com.example.clinicapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.clinicapp.R;
import com.example.clinicapp.activities.MainActivity;
import com.example.clinicapp.database.DatabaseHelper;
import com.example.clinicapp.models.User;

public class LoginFragment extends Fragment {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ((MainActivity) getActivity()).showBottomNavigation(false);

        databaseHelper = new DatabaseHelper(getActivity());

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(v -> loginUser());
        buttonRegister.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new RegisterFragment()));

        return view;
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = databaseHelper.getUser(email, password);
        if (user != null) {
            // Сохраняем ID пользователя в SharedPreferences
            SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            prefs.edit().putInt("user_id", user.getId()).apply();

            Toast.makeText(getActivity(), "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).showBottomNavigation(true);
            ((MainActivity) getActivity()).loadFragment(new HomeFragment());
        } else {
            Toast.makeText(getActivity(), "Неверный email или пароль", Toast.LENGTH_SHORT).show();
        }
    }
}