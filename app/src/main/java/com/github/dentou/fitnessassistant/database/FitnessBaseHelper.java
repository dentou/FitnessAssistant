package com.github.dentou.fitnessassistant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.dentou.fitnessassistant.database.FitnessDbSchema.UserTable;

public class FitnessBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "fitnessBase.db";

    public FitnessBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UserTable.NAME +
                "(" + " _id integer primary key autoincrement, " +
                UserTable.Cols.UUID + ", " +
                UserTable.Cols.NAME + ", " +
                UserTable.Cols.DATE_OF_BIRTH + ", " +
                UserTable.Cols.HEIGHT + ", " +
                UserTable.Cols.WEIGHT + ")"
        );

        // todo create data table containing measurement data
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
