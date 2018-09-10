package com.github.dentou.fitnessassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.dentou.fitnessassistant.model.Body;
import com.github.dentou.fitnessassistant.model.User;
import com.github.dentou.fitnessassistant.worker.BodyHandler;
import com.github.dentou.fitnessassistant.worker.UserHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ProfileEmptyFragment.Callbacks, ProfileFragment.Callbacks, ProgressFragment.Callbacks {

    public static final String TAG = "MainActivity";

    private static final long DRAWER_PROFILE_ID = 1;
    private static final long DRAWER_PROGRESS_ID = 2;
    private static final long DRAWER_SETTINGS_ID = 3;

    private User mUser;

    private Drawer mDrawer;
    private AccountHeader mAccountHeader;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<User> users = UserHandler.get(this).getUsers();
        if (users != null && !users.isEmpty()) {
            mUser = users.get(0);
        }

        if (mUser != null) {
            Body latestBody = BodyHandler.get(MainActivity.this).getLatestBody(mUser.getId());
            if (latestBody != null && !latestBody.isValid()) {
                BodyHandler.get(MainActivity.this).deleteBody(mUser.getId(), latestBody.getId());
            }
        }

        final PrimaryDrawerItem profileDrawerItem = new PrimaryDrawerItem().withIdentifier(DRAWER_PROFILE_ID)
                .withName(R.string.drawer_profile).withIcon(GoogleMaterial.Icon.gmd_person);
        final PrimaryDrawerItem progressDrawerItem = new PrimaryDrawerItem().withIdentifier(DRAWER_PROGRESS_ID)
                .withName(R.string.drawer_progress).withIcon(GoogleMaterial.Icon.gmd_equalizer);
        final PrimaryDrawerItem settingsDrawerItem = new PrimaryDrawerItem().withIdentifier(DRAWER_SETTINGS_ID)
                .withName(R.string.drawer_settings).withIcon(GoogleMaterial.Icon.gmd_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the AccountHeader
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        if (mUser != null) {
            mAccountHeader.addProfiles(new ProfileDrawerItem().withName(mUser.getName())
                    .withIcon(R.drawable.sample_profile).withIdentifier(1 + mAccountHeader.getProfiles().size()));
        }

        //create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(mAccountHeader)
                .withHasStableIds(true)
                .addDrawerItems(
                        profileDrawerItem,
                        progressDrawerItem,
                        new DividerDrawerItem(),
                        settingsDrawerItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (mUser == null) {
                            changeFragment(new ProfileEmptyFragment());
                            return false;
                        }
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == DRAWER_PROFILE_ID) {
                                changeFragment(ProfileFragment.newInstance(mUser.getId()));
                            } else if (drawerItem.getIdentifier() == DRAWER_PROGRESS_ID) {
                                changeFragment(ProgressFragment.newInstance(mUser.getId()));
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        if (savedInstanceState == null) {
            mDrawer.setSelection(DRAWER_PROFILE_ID, true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "OnResume");

        List<User> users = UserHandler.get(this).getUsers();
        if (users != null && !users.isEmpty()) {
            mUser = users.get(0);
        }

        if (mUser != null) {

            IProfile profile = (IProfile) mAccountHeader.getActiveProfile().withName(mUser.getName());
            mAccountHeader.updateProfile(profile);
            Log.i(TAG, "Profile updated");

            mDrawer.setSelection(mDrawer.getCurrentSelection(), true); // Fire OnClickListener to change fragment

        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = mDrawer.saveInstanceState(outState);
        outState = mAccountHeader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void changeFragment(Fragment newFragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, newFragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.fragment_container, newFragment)
                    .commit();
        }

    }

    @Override
    public void onBackPressed() {
        // Handle back press - close the drawer first and if the drawer is closed, close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onProfileCreated(User user) {
        mAccountHeader.addProfiles(new ProfileDrawerItem().withName(user.getName())
                .withIcon(R.drawable.sample_profile));
        Intent intent = ProfileEditActivity.newIntent(this, user.getId());
        startActivity(intent);
    }

    @Override
    public void onProfileEdited(User user) {
        Intent intent = ProfileEditActivity.newIntent(this, user.getId());
        startActivity(intent);
    }

    @Override
    public void onBodyEdited(Body body) {
        Intent intent = BodyEditActivity.newIntent(this, body.getUserId(), body.getId());
        startActivity(intent);
    }
}
