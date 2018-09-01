package com.github.dentou.fitnessassistant.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.github.dentou.fitnessassistant.User;
import com.github.dentou.fitnessassistant.database.FitnessDbSchema.UserTable;

import java.util.Date;
import java.util.UUID;

public class UserCursorWrapper extends CursorWrapper {

    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String uuidString = getString(getColumnIndex(UserTable.Cols.UUID));
        String name = getString(getColumnIndex(UserTable.Cols.NAME));
        long dob = getLong(getColumnIndex(UserTable.Cols.DATE_OF_BIRTH));
        float height = getFloat(getColumnIndex(UserTable.Cols.HEIGHT));
        float weight = getFloat(getColumnIndex(UserTable.Cols.WEIGHT));

        User user = new User(UUID.fromString(uuidString));
        user.setName(name);
        user.setDateOfBirth(new Date(dob));
        user.setHeight(height);
        user.setWeight(weight);

        return user;
    }
}
