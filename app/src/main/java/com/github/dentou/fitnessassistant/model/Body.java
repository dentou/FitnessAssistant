package com.github.dentou.fitnessassistant.model;

import java.util.Date;
import java.util.UUID;

public class Body {
    private final UUID mUserId;
    private final UUID mId;
    private Date mDate;
    private int mBiceps;
    private int mTriceps;
    private int mSubscapular;
    private int mSuprailiac;
    private float mHeight; // in meters
    private float mWeight; // in kg


    public Body(UUID userId) {
        this(userId, UUID.randomUUID());
    }

    public Body(UUID userId, UUID id) {
        mUserId = userId;
        mId = id;
        mDate = new Date();
    }

    public UUID getUserId() {
        return mUserId;
    }

    public UUID getId() {
        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public int getBiceps() {
        return mBiceps;
    }

    public void setBiceps(int biceps) {
        mBiceps = biceps;
    }

    public int getTriceps() {
        return mTriceps;
    }

    public void setTriceps(int triceps) {
        mTriceps = triceps;
    }

    public int getSubscapular() {
        return mSubscapular;
    }

    public void setSubscapular(int subscapular) {
        mSubscapular = subscapular;
    }

    public int getSuprailiac() {
        return mSuprailiac;
    }

    public void setSuprailiac(int suprailiac) {
        mSuprailiac = suprailiac;
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
