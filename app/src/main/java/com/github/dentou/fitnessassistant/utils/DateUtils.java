package com.github.dentou.fitnessassistant.utils;

import org.joda.time.DateTime;

import java.util.Date;

public class DateUtils {

    public static boolean isSameDay(Date first, Date second) {
        if (first == null || second == null) {
            return false;
        }

        DateTime dt1 = new DateTime(first);
        DateTime dt2 = new DateTime(second);

        return dt1.getYear() == dt2.getYear() && dt1.getDayOfYear() == dt2.getDayOfYear();
    }

}
