package com.example.clinicapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.clinicapp.R;
import com.example.clinicapp.database.DatabaseHelper;

public class TestsFragment extends BaseFragment {
    private Spinner spinnerTests;
    private Button buttonOrder;
    private DatabaseHelper databaseHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Инициализируем databaseHelper при присоединении фрагмента
        databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tests, container, false);

        spinnerTests = view.findViewById(R.id.spinnerTests);
        buttonOrder = view.findViewById(R.id.buttonOrder);

        // Настройка списка анализов
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.tests_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTests.setAdapter(adapter);

        buttonOrder.setOnClickListener(v -> {
            if (databaseHelper != null) {
                orderTest();
            } else {
                Toast.makeText(getActivity(), "Ошибка инициализации базы данных", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void orderTest() {
        if (spinnerTests.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "Выберите анализ", Toast.LENGTH_SHORT).show();
            return;
        }

        String test = spinnerTests.getSelectedItem().toString();

        // Получаем ID текущего пользователя
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getActivity(), "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.addTest(userId, test)) {
            Toast.makeText(getActivity(), "Анализ " + test + " заказан", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Ошибка заказа анализа", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем соединение с базой данных
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}