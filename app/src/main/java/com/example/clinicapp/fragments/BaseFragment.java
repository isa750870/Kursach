package com.example.clinicapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clinicapp.R;

import java.text.DateFormat;
import java.util.Date;

public abstract class BaseFragment extends Fragment {
    private TextView timeTextView;
    private Handler timeHandler;
    private Runnable timeUpdater;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view);
    }

    private void setupToolbar(View view) {
        View toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            timeTextView = toolbar.findViewById(R.id.currentTime);
            startTimeUpdates();
        }
    }

    private void startTimeUpdates() {
        timeHandler = new Handler();
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                updateTime();
                timeHandler.postDelayed(this, 1000); // Обновление каждую секунду
            }
        };
        timeHandler.post(timeUpdater);
    }

    private void updateTime() {
        if (timeTextView != null) {
            String time = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date());
            timeTextView.setText(time);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timeHandler != null && timeUpdater != null) {
            timeHandler.removeCallbacks(timeUpdater);
        }
    }

}