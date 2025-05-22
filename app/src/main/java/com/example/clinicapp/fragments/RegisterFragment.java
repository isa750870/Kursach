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

public class RegisterFragment extends Fragment {
    private EditText editTextEmail, editTextPassword, editTextFullName, editTextPhone;
    private Button buttonRegister;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        ((MainActivity) getActivity()).showBottomNavigation(false);

        databaseHelper = new DatabaseHelper(getActivity());

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Валидация email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Введите корректный email");
            return;
        }

        // Валидация пароля (минимум 6 символов)
        if (password.length() < 6) {
            editTextPassword.setError("Пароль должен содержать минимум 6 символов");
            return;
        }

        // Валидация телефона (минимум 10 цифр)
        if (phone.length() < 10 || !phone.matches("\\d+")) {
            editTextPhone.setError("Введите корректный номер телефона");
            return;
        }

        if (databaseHelper.checkUser(email)) {
            Toast.makeText(getActivity(), "Пользователь с таким email уже существует", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(0, email, password, fullName, phone);
        if (databaseHelper.addUser(user)) {
            // Получаем ID нового пользователя
            User registeredUser = databaseHelper.getUser(email, password);
            if (registeredUser != null) {
                // Сохраняем ID пользователя в SharedPreferences
                SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                prefs.edit().putInt("user_id", registeredUser.getId()).apply();
            }

            Toast.makeText(getActivity(), "Регистрация успешна", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).loadFragment(new LoginFragment());
        } else {
            Toast.makeText(getActivity(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
        }
    }
}