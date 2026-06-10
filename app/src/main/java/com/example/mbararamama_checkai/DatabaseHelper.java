package com.example.mbararamama_checkai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MamaHealth.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_HISTORY = "history";

    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_RISK = "risk_level";
    private static final String COL_VITALS = "vitals";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_RISK + " TEXT, " +
                COL_VITALS + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // Save a new record
    public void addRecord(String date, String riskLevel, String vitals) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, date);
        values.put(COL_RISK, riskLevel);
        values.put(COL_VITALS, vitals);
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    // Get all records
    public Cursor getAllRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Order by ID descending so the newest is at the top
        return db.rawQuery("SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COL_ID + " DESC", null);
    }
}
