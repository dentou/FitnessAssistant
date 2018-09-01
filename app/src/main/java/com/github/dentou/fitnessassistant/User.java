package com.github.dentou.fitnessassistant;

import java.util.Date;
import java.util.UUID;

public class User {
    private final UUID mId;
    private String mName;
    private Date mDateOfBirth;
    private float mHeight = 1.5f;
    private float mWeight = 50;

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

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setHeight(float height) {
        mHeight = height;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }

}
