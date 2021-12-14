package com.example.todolist.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "catatan.db";

    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_HOBBY = "hobby";

    public static final String TABLE_TASK = "task";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ISI = "isi";
    public static final String COLUMN_DATE = "date";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_TASK + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY autoincrement, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_ISI + " TEXT NOT NULL," +
                COLUMN_DATE + " TEXT NOT NULL" +
                " )";
        db.execSQL(SQL_CREATE_TASK_TABLE);

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY autoincrement, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_GENDER + " TEXT NOT NULL," +
                COLUMN_AGE + " INTEGER NOT NULL," +
                COLUMN_HOBBY + " TEXT NOT NULL" +
                " )";
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }


    public void insertUser(String username, String gender, int age, String hobby) {
        SQLiteDatabase database = this.getWritableDatabase();
        String queryValues = "INSERT INTO " + TABLE_USER + " (id, username, gender, age, hobby) " +
                "VALUES ('" + 1 + "','" + username + "','" + gender + "'," + age + ",'" + hobby + "')";
        database.execSQL(queryValues);
        Log.i("ds", queryValues);
        database.close();
    }

    public HashMap<String, String> getUser() {
        String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_ID + "= 1 ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        HashMap<String, String> map = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                map.put(COLUMN_USERNAME, cursor.getString(1));
                map.put(COLUMN_GENDER, cursor.getString(2));
                map.put(COLUMN_AGE, cursor.getString(3));
                map.put(COLUMN_HOBBY, cursor.getString(4));
            } while (cursor.moveToNext());
        }
        database.close();
        return map;
    }

    public void updateUser(String username, String gender, int age, String hobby) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_USER + " SET "
                + COLUMN_USERNAME + "='" + username + "', "
                + COLUMN_GENDER + "='" + gender + "', "
                + COLUMN_AGE + "=" + age + ", "
                + COLUMN_HOBBY + "='" + hobby + "'"
                + " WHERE " + COLUMN_ID + "= 1 ";
        database.execSQL(updateQuery);
        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllDataTask() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASK;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(COLUMN_ID, cursor.getString(0));
                map.put(COLUMN_NAME, cursor.getString(1));
                map.put(COLUMN_ISI, cursor.getString(2));
                map.put(COLUMN_DATE, cursor.getString(3));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    public void insertTask(String name, String date, String isi) {
        SQLiteDatabase database = this.getWritableDatabase();
        String queryValues = "INSERT INTO " + TABLE_TASK + " (name, isi, date) " +
                "VALUES ('" + name + "','" + isi + "','" + date + "')";
        database.execSQL(queryValues);
        database.close();
    }

    public void updateTask(int id, String name, String date, String isi) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_TASK + " SET "
                + COLUMN_NAME + "='" + name + "', "
                + COLUMN_DATE + "='" + date + "', "
                + COLUMN_ISI + "='" + isi + "'"
                + " WHERE " + COLUMN_ID + "=" + "'" + id + "'";
        database.execSQL(updateQuery);
        database.close();
    }

    public void deleteTask(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TABLE_TASK + " WHERE " + COLUMN_ID + "=" + "'" + id + "'";
        database.execSQL(updateQuery);
        database.close();
    }


}