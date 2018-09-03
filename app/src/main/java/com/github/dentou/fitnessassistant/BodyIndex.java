package com.github.dentou.fitnessassistant;

import java.util.Date;
import java.util.UUID;

public class BodyIndex {

    private UUID mUserId;
    private UUID mBodyId;
    private Date mDate;
    private float mFatPercentage;

    public BodyIndex(UUID userId, UUID bodyId, Date date) {
        mUserId = userId;
        mBodyId = bodyId;
        mDate = date;
    }

    public float getFatPercentage() {
        return mFatPercentage;
    }

    public void setFatPercentage(float fatPercentage) {
        mFatPercentage = fatPercentage;
    }

    public Date getDate() {
        return mDate;
    }
}
