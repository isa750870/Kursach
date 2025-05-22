package com.example.clinicapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.clinicapp.models.Appointment;
import com.example.clinicapp.models.Doctor;
import com.example.clinicapp.models.TestItem;
import com.example.clinicapp.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "clinic.db";
    private static final int DATABASE_VERSION = 4;

    // Table names
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String KEY_APPOINTMENT_ID = "appointment_id";
    private static final String KEY_APPOINTMENT_DATE = "appointment_date";
    private static final String TABLE_DOCTORS = "doctors";
    private static final String KEY_DOCTOR_ID = "doctor_id";
    private static final String KEY_DOCTOR_NAME = "doctor_name";
    private static final String KEY_SPECIALIZATION = "specialization";
    private static final String KEY_PHOTO_URL = "photo_url";
    private static final String TABLE_TESTS = "tests";
    private static final String KEY_TEST_ID = "test_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TEST_NAME = "test_name";
    private static final String KEY_TEST_DATE = "test_date";
    // User table columns
    private static final String TABLE_USERS = "users";
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_PHONE = "phone";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_FULL_NAME + " TEXT,"
                + KEY_PHONE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
        String CREATE_TESTS_TABLE = "CREATE TABLE " + TABLE_TESTS + "("
                + KEY_TEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_TEST_NAME + " TEXT,"
                + KEY_TEST_DATE + " TEXT,"
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")" + ")";
        db.execSQL(CREATE_TESTS_TABLE);
        String CREATE_DOCTORS_TABLE = "CREATE TABLE " + TABLE_DOCTORS + "("
                + KEY_DOCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DOCTOR_NAME + " TEXT,"
                + KEY_SPECIALIZATION + " TEXT,"
                + KEY_PHOTO_URL + " TEXT" + ")";
        db.execSQL(CREATE_DOCTORS_TABLE);
        String CREATE_APPOINTMENTS_TABLE = "CREATE TABLE " + TABLE_APPOINTMENTS + "("
                + KEY_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_DOCTOR_ID + " INTEGER,"
                + KEY_APPOINTMENT_DATE + " TEXT,"
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "),"
                + "FOREIGN KEY(" + KEY_DOCTOR_ID + ") REFERENCES " + TABLE_DOCTORS + "(" + KEY_DOCTOR_ID + ")" + ")";
        db.execSQL(CREATE_APPOINTMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_FULL_NAME, user.getFullName());
        values.put(KEY_PHONE, user.getPhone());

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }


    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_ID, KEY_EMAIL, KEY_FULL_NAME, KEY_PHONE},
                KEY_EMAIL + "=? AND " + KEY_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    null, // password not returned for security
                    cursor.getString(2),
                    cursor.getString(3)
            );
            cursor.close();
            return user;
        }
        return null;
    }

    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_ID},
                KEY_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_ID, KEY_EMAIL, KEY_PASSWORD, KEY_FULL_NAME, KEY_PHONE},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            return user;
        }
        return null;
    }

    public TestItem getLastUserTest(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        TestItem test = null;

        String query = "SELECT * FROM " + TABLE_TESTS +
                " WHERE " + KEY_USER_ID + " = ?" +
                " ORDER BY " + KEY_TEST_DATE + " DESC" +
                " LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            test = new TestItem(
                    cursor.getInt(0),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        }
        cursor.close();
        return test;
    }
    public boolean addTest(int userId, String testName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Проверяем существование таблицы
        if (!isTableExists(db, TABLE_TESTS)) {
            onCreate(db); // Пересоздаем базу если таблицы нет
        }

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_TEST_NAME, testName);
        values.put(KEY_TEST_DATE, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

        long result = db.insert(TABLE_TESTS, null, values);
        db.close();
        return result != -1;
    }

    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean deleteTestById(int testId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TESTS,
                KEY_TEST_ID + "=?",
                new String[]{String.valueOf(testId)}) > 0;
    }

    // Обновляем метод получения анализов
    public List<TestItem> getUserTests(int userId) {
        List<TestItem> tests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TESTS,
                new String[]{KEY_TEST_ID, KEY_TEST_NAME, KEY_TEST_DATE},
                KEY_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, KEY_TEST_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                TestItem item = new TestItem(
                        cursor.getInt(0), // ID
                        cursor.getString(1), // Название
                        cursor.getString(2) // Дата
                );
                tests.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tests;
    }
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOCTORS,
                new String[]{KEY_DOCTOR_ID, KEY_DOCTOR_NAME, KEY_SPECIALIZATION, KEY_PHOTO_URL},
                null, null, null, null, KEY_DOCTOR_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Doctor doctor = new Doctor(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                doctors.add(doctor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return doctors;
    }
    public boolean addDoctor(Doctor doctor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DOCTOR_NAME, doctor.getName());
        values.put(KEY_SPECIALIZATION, doctor.getSpecialization());
        values.put(KEY_PHOTO_URL, doctor.getPhotoUrl());

        long result = db.insert(TABLE_DOCTORS, null, values);
        db.close();
        return result != -1;
    }
    public boolean addAppointment(int userId, int doctorId, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_DOCTOR_ID, doctorId);
        values.put(KEY_APPOINTMENT_DATE, dateTime);

        long result = db.insert(TABLE_APPOINTMENTS, null, values);
        db.close();
        return result != -1;
    }

    // Метод для получения записей пользователя
    public List<Appointment> getUserAppointments(int userId) {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT a.*, d." + KEY_DOCTOR_NAME + ", d." + KEY_SPECIALIZATION +
                " FROM " + TABLE_APPOINTMENTS + " a" +
                " JOIN " + TABLE_DOCTORS + " d ON a." + KEY_DOCTOR_ID + " = d." + KEY_DOCTOR_ID +
                " WHERE a." + KEY_USER_ID + " = ?" +
                " ORDER BY a." + KEY_APPOINTMENT_DATE + " DESC" +
                " LIMIT 3";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        if (cursor.moveToFirst()) {
            do {
                try {
                    Date date = dateFormat.parse(cursor.getString(3));
                    Appointment appointment = new Appointment(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getString(4),
                            cursor.getString(5),
                            date
                    );
                    appointments.add(appointment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return appointments;
    }
    public boolean deleteAppointment(int appointmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_APPOINTMENTS,
                KEY_APPOINTMENT_ID + "=?",
                new String[]{String.valueOf(appointmentId)});
        db.close();
        return result > 0;
    }
    public boolean checkUserPassword(int userId, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_PASSWORD},
                KEY_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String storedPassword = cursor.getString(0);
            cursor.close();
            return storedPassword.equals(password);
        }
        return false;
    }

    // Обновление данных пользователя
    public boolean updateUser(int userId, String fullName, String email, String phone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FULL_NAME, fullName);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PHONE, phone);

        if (newPassword != null) {
            values.put(KEY_PASSWORD, newPassword);
        }

        int result = db.update(TABLE_USERS, values,
                KEY_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }
}