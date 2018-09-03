package com.github.dentou.fitnessassistant.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.github.dentou.fitnessassistant.Body;
import com.github.dentou.fitnessassistant.database.FitnessDbSchema.BodyTable;

import java.util.Date;
import java.util.UUID;

public class BodyCursorWrapper extends CursorWrapper {

    public BodyCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Body getBody() {
        String userUuidString = getString(getColumnIndex(BodyTable.Cols.USER_UUID));
        String uuidStringg = getString(getColumnIndex(BodyTable.Cols.UUID));
        long date = getLong(getColumnIndex(BodyTable.Cols.DATE));
        int frontArm = getInt(getColumnIndex(BodyTable.Cols.BICEPS));
        int backArm = getInt(getColumnIndex(BodyTable.Cols.TRICEPS));
        int back = getInt(getColumnIndex(BodyTable.Cols.SUBSCAPULAR));
        int waist = getInt(getColumnIndex(BodyTable.Cols.SUPRAILIAC));
        float height = getFloat(getColumnIndex(BodyTable.Cols.HEIGHT));
        float weight = getFloat(getColumnIndex(BodyTable.Cols.WEIGHT));

        Body body = new Body(UUID.fromString(userUuidString), UUID.fromString(uuidStringg));
        body.setDate(new Date(date));
        body.setBiceps(frontArm);
        body.setTriceps(backArm);
        body.setSubscapular(back);
        body.setSuprailiac(waist);
        body.setHeight(height);
        body.setWeight(weight);

        return body;
    }
}
