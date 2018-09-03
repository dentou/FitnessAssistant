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
        int gender = getInt(getColumnIndex(UserTable.Cols.GENDER));
        long dob = getLong(getColumnIndex(UserTable.Cols.DATE_OF_BIRTH));


        User user = new User(UUID.fromString(uuidString));
        user.setName(name);
        user.setGender(gender);
        user.setDateOfBirth(new Date(dob));

        return user;
    }
}
