package com.github.dentou.fitnessassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.dentou.fitnessassistant.database.BodyCursorWrapper;
import com.github.dentou.fitnessassistant.database.FitnessBaseHelper;
import com.github.dentou.fitnessassistant.database.FitnessDbSchema.BodyTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BodyHandler {

    private static BodyHandler sBodyHandler;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private BodyHandler(Context context) {
        mContext = context;
        mDatabase = new FitnessBaseHelper(context).getWritableDatabase();
    }

    public static BodyHandler get(Context context) {
        if (sBodyHandler == null) {
            sBodyHandler = new BodyHandler(context);
        }

        return sBodyHandler;
    }

    public void addBody(Body body) {
        ContentValues values = getContentValues(body);

        mDatabase.insert(BodyTable.NAME, null, values);
    }

    public void deleteBody(UUID userId, UUID bodyId) {
        mDatabase.delete(BodyTable.NAME,
                BodyTable.Cols.USER_UUID + " = ? and " + BodyTable.Cols.UUID + " = ?",
                new String[] {userId.toString(), bodyId.toString()});
    }

    public Body getBody(UUID userId, UUID id) {
        BodyCursorWrapper cursor = queryBodies(
                BodyTable.Cols.USER_UUID + " = ? and " + BodyTable.Cols.UUID + " = ?",
                new String[] {userId.toString(), id.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBody();
        } finally {
            cursor.close();
        }
    }

    public List<Body> getBodies(UUID userId) {
        List<Body> bodies =  new ArrayList<>();

        BodyCursorWrapper cursor = queryBodies(
                BodyTable.Cols.USER_UUID + " = ?",
                new String[] { userId.toString() });
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                bodies.add(cursor.getBody());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return bodies;
    }


    public void updateBody(Body body) {
        String userUuidString = body.getUserId().toString();
        String uuidString = body.getId().toString();
        ContentValues values = getContentValues(body);

        mDatabase.update(BodyTable.NAME, values,
                BodyTable.Cols.USER_UUID + " = ? and " + BodyTable.Cols.UUID + " = ?",
                new String[] { userUuidString, uuidString });
    }

    private BodyCursorWrapper queryBodies(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                BodyTable.NAME,
                null, // columns = null means select all columns
                whereClause,
                whereArgs,
                null,
                null,
                BodyTable.Cols.DATE + " ASC"
        );

        return new BodyCursorWrapper(cursor);
    }


    private static ContentValues getContentValues(Body body) {
        ContentValues values = new ContentValues();
        values.put(BodyTable.Cols.USER_UUID, body.getUserId().toString());
        values.put(BodyTable.Cols.UUID, body.getId().toString());
        values.put(BodyTable.Cols.DATE, body.getDate().getTime());
        values.put(BodyTable.Cols.BICEPS, body.getBiceps());
        values.put(BodyTable.Cols.TRICEPS, body.getTriceps());
        values.put(BodyTable.Cols.SUBSCAPULAR, body.getSubscapular());
        values.put(BodyTable.Cols.SUPRAILIAC, body.getSuprailiac());
        values.put(BodyTable.Cols.HEIGHT, body.getHeight());
        values.put(BodyTable.Cols.WEIGHT, body.getWeight());

        return values;
    }
}
