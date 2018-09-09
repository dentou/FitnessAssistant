package com.github.dentou.fitnessassistant.model;

import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.Date;
import java.util.UUID;

public class User {

    public static final int FEMALE = 0;
    public static final int MALE = 1;

    private final UUID mId;
    private String mName;
    private int mGender = FEMALE;
    private Date mDateOfBirth;

    public User() {
        this(UUID.randomUUID());
    }

    public User(UUID id) {
        mId = id;
        mDateOfBirth = new Date();
        mName = "User";
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    public int getAge() {
        DateTime dob = new DateTime(mDateOfBirth);
        DateTime current = new DateTime(new Date());
        return Years.yearsBetween(dob, current).getYears();
    }

}
