package com.github.dentou.fitnessassistant.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.dentou.fitnessassistant.database.FitnessDbSchema.BodyTable;
import com.github.dentou.fitnessassistant.database.FitnessDbSchema.UserTable;

public class FitnessBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "fitnessBase.db";

    public FitnessBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create table containing users' basic info
        db.execSQL("create table " + UserTable.NAME +
                "(" + " _id integer primary key autoincrement, " +
                UserTable.Cols.UUID + ", " +
                UserTable.Cols.NAME + ", " +
                UserTable.Cols.GENDER + ", " +
                UserTable.Cols.DATE_OF_BIRTH + ")"
        );

        // Create table containing users' body info
        db.execSQL("create table " + BodyTable.NAME +
                "(" + " _id integer primary key autoincrement, " +
                BodyTable.Cols.USER_UUID + ", " +
                BodyTable.Cols.UUID + ", " +
                BodyTable.Cols.DATE + ", " +
                BodyTable.Cols.BICEPS + ", " +
                BodyTable.Cols.TRICEPS + ", " +
                BodyTable.Cols.SUBSCAPULAR + ", " +
                BodyTable.Cols.SUPRAILIAC + ", " +
                BodyTable.Cols.HEIGHT + ", " +
                BodyTable.Cols.WEIGHT + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
