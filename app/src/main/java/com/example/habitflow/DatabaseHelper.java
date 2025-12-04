package com.example.habitflow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "habitflow.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_FIRSTNAME = "firstname";
    private static final String COL_LASTNAME = "lastname";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_FIRSTNAME + " TEXT," +
                COL_LASTNAME + " TEXT," +
                COL_EMAIL + " TEXT UNIQUE," +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    //  Ajouter un utilisateur
    public boolean addUser(String firstname, String lastname, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIRSTNAME, firstname);
        values.put(COL_LASTNAME, lastname);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_USERS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result != -1;
    }

    //  Vérifier login
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=? AND " + COL_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //  Vérifier si un email existe déjà
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //  Récupérer le mot de passe par email
    public String getPasswordByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String password = null;
        if (cursor.moveToFirst()) {
            password = cursor.getString(0);
        }
        cursor.close();
        return password;
    }

    //  Récupérer un utilisateur complet
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?";
        return db.rawQuery(query, new String[]{email});
    }

    // Récupérer le prénom + nom pour le Dashboard
    public String getUserFullNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_FIRSTNAME + ", " + COL_LASTNAME +
                " FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String fullName = null;

        if (cursor.moveToFirst()) {
            String firstname = cursor.getString(0);
            String lastname = cursor.getString(1);
            fullName = firstname + " " + lastname;
        }

        cursor.close();
        return fullName;
    }
}
