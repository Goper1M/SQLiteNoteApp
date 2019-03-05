package com.spoonexample.sqlitenoteapp1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.spoonexample.sqlitenoteapp1.Note.*;

import static com.spoonexample.sqlitenoteapp1.Note.NoteEntry.TABLE_NAME;

public class NoteDataBase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "notes.db";

    public NoteDataBase(Context context) {
        // make the database
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating the table inside the database
        final String SQL_CREATE_NOTE_TABLE = "CREATE table " +
                NoteEntry.TABLE_NAME + "(" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                NoteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_BODY + " TEXT NOT NULL)";
        db.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    // not really sure what this does..
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    // might not use this anymore...?
    // this is now used in NoteTaking.class
    // keeping this here so i know of another way to do it.
    public boolean insertData(String title, String body) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //putting the title and body in contentValues
        contentValues.put(NoteEntry.COLUMN_TITLE, title);
        contentValues.put(NoteEntry.COLUMN_BODY, body);
        //inserting our data into the db instance using keyword insert --- isInserted will return -1 if successful
        long isInserted = db.insert(TABLE_NAME, null, contentValues);
        //checking to see if our data has been inserted, will return true if successful and false otherwise.
        if (isInserted == -1) {
            return true;
        } else {
            return false;
        }
    }

    // this might not be used anymore
    // correct this is not needed anymore.
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor allData = db.rawQuery("select * from " + TABLE_NAME, null);
        return allData;
    }
}



