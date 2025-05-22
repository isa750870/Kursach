package com.example.clinicapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.clinicapp.R;
import com.example.clinicapp.activities.MainActivity;
import com.example.clinicapp.adapters.AppointmentPagerAdapter;
import com.example.clinicapp.database.DatabaseHelper;
import com.example.clinicapp.models.Appointment;
import com.example.clinicapp.models.TestItem;
import com.example.clinicapp.models.User;

import java.util.List;

public class HomeFragment extends BaseFragment  {
    private ViewPager appointmentsPager;
    private LinearLayout dotsLayout;
    private TextView noAppointmentsText;
    private DatabaseHelper databaseHelper;

    private CardView userCard, lastTestCard;
    private TextView userName, userEmail, lastTestName, lastTestDate;
    private int currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        databaseHelper = new DatabaseHelper(getActivity());

        // Получаем ID текущего пользователя
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);
        if (currentUserId == -1) {
            Toast.makeText(getActivity(), "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Загрузка данных
        loadUserData();
        loadLastTest();
        loadAppointments();

        return view;
    }

    private void initViews(View view) {
        appointmentsPager = view.findViewById(R.id.appointmentsPager);
        dotsLayout = view.findViewById(R.id.dotsLayout);
        noAppointmentsText = view.findViewById(R.id.noAppointmentsText);
        userCard = view.findViewById(R.id.userCard);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        lastTestCard = view.findViewById(R.id.lastTestCard);
        lastTestName = view.findViewById(R.id.lastTestName);
        lastTestDate = view.findViewById(R.id.lastTestDate);
    }

    private void loadAppointments() {
        if (currentUserId == -1) return;

        List<Appointment> appointments = databaseHelper.getUserAppointments(currentUserId);

        if (appointments.isEmpty()) {
            noAppointmentsText.setVisibility(View.VISIBLE);
            appointmentsPager.setVisibility(View.GONE);
            dotsLayout.setVisibility(View.GONE);
        } else {
            noAppointmentsText.setVisibility(View.GONE);
            appointmentsPager.setVisibility(View.VISIBLE);
            dotsLayout.setVisibility(View.VISIBLE);

            AppointmentPagerAdapter adapter = new AppointmentPagerAdapter(getActivity(), appointments);
            appointmentsPager.setAdapter(adapter);
            setupDots(appointments.size());

            appointmentsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    updateDots(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {}
            });
        }
    }

    private void loadUserData() {
        User user = databaseHelper.getUserById(currentUserId);
        if (user != null) {
            userName.setText(user.getFullName());
            userEmail.setText(user.getEmail());
        }
    }

    private void loadLastTest() {
        if (lastTestCard == null || lastTestName == null || lastTestDate == null) return;

        TestItem lastTest = databaseHelper.getLastUserTest(currentUserId);
        if (lastTest != null) {
            lastTestName.setText(lastTest.getName());
            lastTestDate.setText("Заказан: " + lastTest.getDate());
            lastTestCard.setVisibility(View.VISIBLE);
        } else {
            lastTestCard.setVisibility(View.GONE);
        }
    }

    private void setupDots(int count) {
        dotsLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            View dot = new View(getActivity());
            dot.setBackgroundResource(R.drawable.dot_unselected);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (8 * getResources().getDisplayMetrics().density),
                    (int) (8 * getResources().getDisplayMetrics().density));
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);

            dotsLayout.addView(dot);
        }

        updateDots(0);
    }

    private void updateDots(int position) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            View dot = dotsLayout.getChildAt(i);
            dot.setBackgroundResource(i == position ? R.drawable.dot_selected : R.drawable.dot_unselected);
        }
    }
    public void refreshAppointments() {
        loadAppointments();
    }
}