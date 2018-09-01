package com.github.dentou.fitnessassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.dentou.fitnessassistant.database.FitnessBaseHelper;
import com.github.dentou.fitnessassistant.database.FitnessDbSchema.UserTable;
import com.github.dentou.fitnessassistant.database.UserCursorWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserHandler {

    private static UserHandler mUserHandler;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private UserHandler(Context context) {
        mContext = context;
        mDatabase = new FitnessBaseHelper(context).getWritableDatabase();
    }

    public static UserHandler get(Context context) {
        if (mUserHandler == null) {
            mUserHandler = new UserHandler(context);
        }
        return mUserHandler;
    }

    public void addUser(User user) {
        ContentValues values = getContentValues(user);

        mDatabase.insert(UserTable.NAME, null, values);
    }

    public List<User> getUsers() {
        List<User> users =  new ArrayList<>();

        UserCursorWrapper cursor = queryUsers(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                users.add(cursor.getUser());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return users;
    }

    public User getUser(UUID id) {
        UserCursorWrapper cursor = queryUsers(
                UserTable.Cols.UUID + " = ?",
                new String[] {id.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getUser();
        } finally {
            cursor.close();
        }
    }

    public void updateUser(User user) {
        String uuidString = user.getId().toString();
        ContentValues values = getContentValues(user);

        mDatabase.update(UserTable.NAME, values,
                UserTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }


    private UserCursorWrapper queryUsers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                UserTable.NAME,
                null, // columns = null means select all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new UserCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.Cols.UUID, user.getId().toString());
        values.put(UserTable.Cols.NAME, user.getName());
        values.put(UserTable.Cols.DATE_OF_BIRTH, user.getDateOfBirth().getTime());
        values.put(UserTable.Cols.HEIGHT, user.getHeight());
        values.put(UserTable.Cols.WEIGHT, user.getWeight());

        return values;
    }

}
