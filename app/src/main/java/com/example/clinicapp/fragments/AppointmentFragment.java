package com.example.clinicapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.clinicapp.R;
import com.example.clinicapp.activities.MainActivity;
import com.example.clinicapp.database.DatabaseHelper;
import com.example.clinicapp.models.Doctor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentFragment extends BaseFragment {
    private Spinner doctorSpinner;
    private TextView dateTextView, timeTextView;
    private Button dateButton, timeButton, submitButton;
    private DatabaseHelper databaseHelper;
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        databaseHelper = new DatabaseHelper(getActivity());

        // Инициализация UI элементов
        doctorSpinner = view.findViewById(R.id.doctorSpinner);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        dateButton = view.findViewById(R.id.dateButton);
        timeButton = view.findViewById(R.id.timeButton);
        submitButton = view.findViewById(R.id.submitButton);

        // Загрузка списка врачей
        loadDoctors();

        // Установка текущей даты и времени по умолчанию
        updateDateTimeViews();

        // Обработчики событий
        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());
        submitButton.setOnClickListener(v -> submitAppointment());

        return view;
    }

    private void loadDoctors() {
        List<Doctor> doctors = databaseHelper.getAllDoctors();
        ArrayAdapter<Doctor> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                doctors
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorSpinner.setAdapter(adapter);
    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(
                getActivity(),
                this::onDateSet,
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );

        // Установка минимальной даты (сегодня)
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void onDateSet(DatePicker view, int year, int month, int day) {
        selectedDateTime.set(Calendar.YEAR, year);
        selectedDateTime.set(Calendar.MONTH, month);
        selectedDateTime.set(Calendar.DAY_OF_MONTH, day);
        updateDateTimeViews();
    }

    private void showTimePicker() {
        TimePickerDialog timePicker = new TimePickerDialog(
                getActivity(),
                this::onTimeSet,
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );
        timePicker.show();
    }

    private void onTimeSet(TimePicker view, int hour, int minute) {
        selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
        selectedDateTime.set(Calendar.MINUTE, minute);
        updateDateTimeViews();
    }

    private void updateDateTimeViews() {
        // Форматирование даты
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("ru"));
        dateTextView.setText(dateFormat.format(selectedDateTime.getTime()));

        // Форматирование времени
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeTextView.setText(timeFormat.format(selectedDateTime.getTime()));
    }

    private void submitAppointment() {
        Doctor selectedDoctor = (Doctor) doctorSpinner.getSelectedItem();

        if (selectedDoctor == null) {
            Toast.makeText(getActivity(), "Выберите врача", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем ID текущего пользователя
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getActivity(), "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            return;
        }

        // Форматирование даты и времени для сохранения
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String appointmentDateTime = dbDateFormat.format(selectedDateTime.getTime());

        if (databaseHelper.addAppointment(userId, selectedDoctor.getId(), appointmentDateTime)) {
            Toast.makeText(getActivity(),
                    "Запись к " + selectedDoctor.getName() +
                            " на " + appointmentDateTime + " создана",
                    Toast.LENGTH_LONG).show();

            // Возвращаемся на главный экран
            ((MainActivity) getActivity()).loadFragment(new HomeFragment());
        } else {
            Toast.makeText(getActivity(), "Ошибка создания записи", Toast.LENGTH_SHORT).show();
        }
    }

}