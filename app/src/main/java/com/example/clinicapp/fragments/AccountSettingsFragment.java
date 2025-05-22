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
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;

import com.example.clinicapp.R;
import com.example.clinicapp.database.DatabaseHelper;
import com.example.clinicapp.models.User;

public class AccountSettingsFragment extends Fragment {
    private EditText editFullName, editEmail, editPhone, editCurrentPassword, editNewPassword;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private int currentUserId;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);

        databaseHelper = new DatabaseHelper(getActivity());

        // Получаем ID текущего пользователя
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(getActivity(), "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
            return view;
        }

        // Инициализация UI
        editFullName = view.findViewById(R.id.editFullName);
        editEmail = view.findViewById(R.id.editEmail);
        editPhone = view.findViewById(R.id.editPhone);
        editCurrentPassword = view.findViewById(R.id.editCurrentPassword);
        editNewPassword = view.findViewById(R.id.editNewPassword);
        btnSave = view.findViewById(R.id.btnSave);

        // Загружаем данные пользователя
        loadUserData();

        btnSave.setOnClickListener(v -> updateUserData());
        // В onCreateView AccountSettingsFragment
        return view;
    }

    private void loadUserData() {
        currentUser = databaseHelper.getUserById(currentUserId);
        if (currentUser != null) {
            editFullName.setText(currentUser.getFullName());
            editEmail.setText(currentUser.getEmail());
            editPhone.setText(currentUser.getPhone());
        }
    }

    private void updateUserData() {
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String currentPassword = editCurrentPassword.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();

        // Валидация данных
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getActivity(), "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Введите корректный email");
            return;
        }

        // Проверка пароля (если введен текущий)
        if (!currentPassword.isEmpty()) {
            if (!databaseHelper.checkUserPassword(currentUserId, currentPassword)) {
                editCurrentPassword.setError("Неверный текущий пароль");
                return;
            }

            if (newPassword.isEmpty() || newPassword.length() < 6) {
                editNewPassword.setError("Новый пароль должен содержать минимум 6 символов");
                return;
            }
        }

        // Обновление данных пользователя
        if (databaseHelper.updateUser(currentUserId, fullName, email, phone,
                !newPassword.isEmpty() ? newPassword : null)) {
            Toast.makeText(getActivity(), "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(getActivity(), "Ошибка обновления данных", Toast.LENGTH_SHORT).show();
        }
    }

}