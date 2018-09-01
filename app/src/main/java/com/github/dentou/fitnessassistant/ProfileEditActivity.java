package com.github.dentou.fitnessassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

public class ProfileEditActivity extends AppCompatActivity {

    private static final String EXTRA_USER_ID = "com.github.dentou.fitnessassistant.user_id";

    public static Intent newIntent(Context packageContext, UUID userId) {
        Intent intent = new Intent(packageContext, ProfileEditActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);


        UUID userId = (UUID) getIntent().getSerializableExtra(EXTRA_USER_ID);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = ProfileEditFragment.newInstance(userId);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }
}
