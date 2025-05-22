package com.example.clinicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.clinicapp.R;
import com.example.clinicapp.activities.MainActivity;
import com.example.clinicapp.models.Appointment;

import java.util.List;

public class AppointmentPagerAdapter extends PagerAdapter {
    private Context context;
    private List<Appointment> appointments;

    public AppointmentPagerAdapter(Context context, List<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    private OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public AppointmentPagerAdapter(Context context, List<Appointment> appointments, OnAppointmentClickListener listener) {
        this.context = context;
        this.appointments = appointments;
        this.listener = listener;
    }

// В методе instantiateItem добавляем обработчик клика


    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_appointment_card, container, false);

        Appointment appointment = appointments.get(position);

        TextView doctorName = view.findViewById(R.id.doctorName);
        TextView doctorSpecialization = view.findViewById(R.id.doctorSpecialization);
        TextView appointmentDay = view.findViewById(R.id.appointmentDay);
        TextView appointmentDate = view.findViewById(R.id.appointmentDate);
        TextView appointmentTime = view.findViewById(R.id.appointmentTime);

        doctorName.setText(appointment.getDoctorName());
        doctorSpecialization.setText(appointment.getDoctorSpecialization());
        appointmentDay.setText(appointment.getFormattedDay());
        appointmentDate.setText(appointment.getFormattedDate());
        appointmentTime.setText(appointment.getFormattedTime());
        view.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).showDeleteAppointmentDialog(appointments.get(position));
            }
        });
        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).showDeleteAppointmentDialog(appointments.get(position));
            }
        });
        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}