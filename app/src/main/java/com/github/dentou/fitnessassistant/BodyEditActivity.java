package com.github.dentou.fitnessassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.UUID;

public class BodyEditActivity extends AppCompatActivity {

    public static final String TAG = "BodyEditActivity";

    private static final String EXTRA_USER_ID = "com.github.dentou.fitnessassistant.user_id";
    private static final String EXTRA_BODY_ID = "com.github.dentou.fitnessassistant.body_id";

    private Body mBody;

    public static Intent newIntent(Context packageContext, UUID userId, UUID bodyId) {
        Intent intent = new Intent(packageContext, BodyEditActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_BODY_ID, bodyId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);


        UUID userId = (UUID) getIntent().getSerializableExtra(EXTRA_USER_ID);
        UUID bodyId = (UUID) getIntent().getSerializableExtra(EXTRA_BODY_ID);
        mBody = BodyHandler.get(this).getBody(userId, bodyId);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = BodyEditFragment.newInstance(userId, bodyId);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                deleteInvalidBody();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        deleteInvalidBody();
        super.onBackPressed();
    }

    private void deleteInvalidBody() {
        mBody = BodyHandler.get(this).getBody(mBody.getUserId(), mBody.getId());
        if (mBody.getBiceps() <= 0
                || mBody.getTriceps() <= 0
                || mBody.getSubscapular() <= 0
                || mBody.getSuprailiac() <= 0
                || mBody.getHeight() <= 0
                || mBody.getWeight() <= 0) {

            Log.i(TAG, "Body deleted");
            BodyHandler.get(this).deleteBody(mBody.getUserId(), mBody.getId());
        }
    }
}
