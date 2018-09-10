package com.github.dentou.fitnessassistant.model;

import java.util.Date;
import java.util.UUID;

public class BodyIndex {

    private UUID mUserId;
    private UUID mBodyId;
    private Date mDate;
    private float mFatPercentage;
    private float mFatMass;
    private float mLeanMuscleMass;
    private float mBMI;
    private float mBMR;

    public BodyIndex(UUID userId, UUID bodyId, Date date) {
        mUserId = userId;
        mBodyId = bodyId;
        mDate = date;
    }

    public UUID getUserId() {
        return mUserId;
    }

    public UUID getBodyId() {
        return mBodyId;
    }

    public Date getDate() {
        return mDate;
    }

    public float getFatPercentage() {
        return mFatPercentage;
    }

    public void setFatPercentage(float fatPercentage) {
        mFatPercentage = fatPercentage;
    }

    public float getFatMass() {
        return mFatMass;
    }

    public void setFatMass(float fatMass) {
        mFatMass = fatMass;
    }

    public float getLeanMuscleMass() {
        return mLeanMuscleMass;
    }

    public void setLeanMuscleMass(float leanMuscleMass) {
        mLeanMuscleMass = leanMuscleMass;
    }

    public float getBMI() {
        return mBMI;
    }

    public void setBMI(float BMI) {
        mBMI = BMI;
    }

    public float getBMR() {
        return mBMR;
    }

    public void setBMR(float BMR) {
        mBMR = BMR;
    }
}
