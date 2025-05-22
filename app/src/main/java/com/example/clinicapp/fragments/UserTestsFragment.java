package com.example.clinicapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.clinicapp.R;
import com.example.clinicapp.database.DatabaseHelper;
import com.example.clinicapp.models.TestItem;

import java.util.List;

public class UserTestsFragment extends Fragment {
    private ListView testsListView;
    private DatabaseHelper databaseHelper;
    private int currentUserId;
    private List<TestItem> testsList;
    private TestListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_tests, container, false);

        try {
            // Инициализация databaseHelper
            databaseHelper = new DatabaseHelper(requireActivity());

            // Получаем ID текущего пользователя
            SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            currentUserId = prefs.getInt("user_id", -1);

            testsListView = view.findViewById(R.id.testsListView);
            Button backButton = view.findViewById(R.id.backButton);

            loadUserTests();

            testsListView.setOnItemClickListener((parent, view1, position, id) -> {
                TestItem selectedTest = testsList.get(position);
                showDeleteConfirmationDialog(selectedTest);
            });

            backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void loadUserTests() {
        try {
            if (currentUserId != -1) {
                testsList = databaseHelper.getUserTests(currentUserId);
                if (testsList != null) {
                    adapter = new TestListAdapter(requireActivity(), testsList);
                    testsListView.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Ошибка загрузки анализов: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(TestItem testItem) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Удаление анализа")
                .setMessage("Вы уверены, что хотите удалить " + testItem.getName() + "?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    if (databaseHelper.deleteTestById(testItem.getId())) {
                        testsList.remove(testItem);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Анализ удален", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    // Кастомный адаптер для списка анализов
    private static class TestListAdapter extends ArrayAdapter<TestItem> {
        TestListAdapter(Context context, List<TestItem> tests) {
            super(context, android.R.layout.simple_list_item_1, tests);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = view.findViewById(android.R.id.text1);
            TestItem item = getItem(position);
            if (item != null) {
                textView.setText(item.getName() + " (заказан " + item.getDate() + ")");
            }
            return view;
        }
    }
}