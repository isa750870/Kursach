package com.example.clinicapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.clinicapp.R;
import com.example.clinicapp.database.DatabaseHelper;
import com.example.clinicapp.fragments.AccountSettingsFragment;
import com.example.clinicapp.fragments.AppointmentFragment;
import com.example.clinicapp.fragments.HomeFragment;
import com.example.clinicapp.fragments.LoginFragment;
import com.example.clinicapp.fragments.ProfileFragment;
import com.example.clinicapp.fragments.TestsFragment;
import com.example.clinicapp.fragments.UserTestsFragment;
import com.example.clinicapp.models.Appointment;
import com.example.clinicapp.models.Doctor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeTestDoctors();
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_appointment) {
                loadFragment(new AppointmentFragment());
                return true;
            } else if (itemId == R.id.nav_tests) {
                loadFragment(new TestsFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
        // Load login fragment by default
        loadFragment(new LoginFragment());
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showBottomNavigation(boolean show) {
        bottomNavigationView.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    private void initializeTestDoctors() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Проверяем, есть ли уже врачи в базе
        if (dbHelper.getAllDoctors().isEmpty()) {
            dbHelper.addDoctor(new Doctor(0, "Быков А.Е.", "Терапевт", ""));
            dbHelper.addDoctor(new Doctor(0, "Купитман И.Н.", "Уролог", ""));
            dbHelper.addDoctor(new Doctor(0, "Левин Б.А.", "Кардиолог", ""));
            dbHelper.addDoctor(new Doctor(0, "Черноус В.Н.", "Невролог", ""));
            dbHelper.addDoctor(new Doctor(0, "Романенко Г.В.", "Хирург", ""));
            dbHelper.addDoctor(new Doctor(0, "Лобанов С.С.", "Массажист", ""));
        }
        dbHelper.close();
    }
    public void showDeleteAppointmentDialog(Appointment appointment) {
        new AlertDialog.Builder(this)
                .setTitle("Отмена записи")
                .setMessage("Вы уверены, что хотите отменить запись к " +
                        appointment.getDoctorName() + " на " +
                        appointment.getFormattedDateTimeForDialog() + "?")
                .setPositiveButton("Отменить запись", (dialog, which) -> {
                    deleteAppointment(appointment);
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void deleteAppointment(Appointment appointment) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (dbHelper.deleteAppointment(appointment.getId())) {
            Toast.makeText(this, "Запись отменена", Toast.LENGTH_SHORT).show();
            // Обновляем фрагмент
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current instanceof HomeFragment) {
                ((HomeFragment) current).refreshAppointments();
            }
        } else {
            Toast.makeText(this, "Ошибка при отмене записи", Toast.LENGTH_SHORT).show();
        }
        dbHelper.close();
    }

    public void openProfileSettings(View view) {
        loadFragment(new AccountSettingsFragment());
    }

    public void openTestsList(View view) {
        loadFragment(new UserTestsFragment());
    }
}