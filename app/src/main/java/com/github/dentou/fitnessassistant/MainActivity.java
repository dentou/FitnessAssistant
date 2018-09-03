package com.github.dentou.fitnessassistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

    private static final String EXTRA_DRAWER_ITEM_ID = "com.github.dentou.fitnessassistant.drawer_item";

    private static final long DRAWER_PROFILE_ID = 1;
    private static final long DRAWER_PROGRESS_ID = 2;
    private static final long DRAWER_SETTINGS_ID = 3;

    private User mUser;

    private Drawer mDrawer;

    public static Intent newIntent(Context packageContext, long drawerItemId) {
        Intent intent = new Intent(packageContext, BodyEditActivity.class);
        intent.putExtra(EXTRA_DRAWER_ITEM_ID, drawerItemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<User> users = UserHandler.get(this).getUsers();
        if (users != null && !users.isEmpty()) {
            mUser = users.get(0);
        }

        long drawerItemId = getIntent().getLongExtra(EXTRA_DRAWER_ITEM_ID, DRAWER_PROFILE_ID);

        final PrimaryDrawerItem profileDrawerItem = new PrimaryDrawerItem().withIdentifier(DRAWER_PROFILE_ID)
                .withName(R.string.drawer_profile).withIcon(GoogleMaterial.Icon.gmd_person);
        final PrimaryDrawerItem progressDrawerItem = new PrimaryDrawerItem().withIdentifier(DRAWER_PROGRESS_ID)
                .withName(R.string.drawer_progress).withIcon(GoogleMaterial.Icon.gmd_equalizer);
        final PrimaryDrawerItem settingsDrawerItem = new PrimaryDrawerItem().withIdentifier(DRAWER_SETTINGS_ID)
                .withName(R.string.drawer_settings).withIcon(GoogleMaterial.Icon.gmd_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Huy Tran").withEmail("huytran.ee@gmail.com")
                                .withIcon(R.drawable.sample_profile)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
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

        mDrawer.setSelection(drawerItemId, true);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mUser == null) {
            List<User> users = UserHandler.get(this).getUsers();
            if (users != null && !users.isEmpty()) {
                mUser = users.get(0);
            }
            if (mUser != null) {
                mDrawer.setSelection(mDrawer.getCurrentSelection(), true);
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = mDrawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
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
        Intent intent = ProfileEditActivity.newIntent(this, user.getId());
        startActivity(intent);
    }

    @Override
    public void onProfileEdited(User user) {
        Intent intent = ProfileEditActivity.newIntent(this, user.getId());
        startActivity(intent);
    }

    @Override
    public void onBodyCreated(Body body) {
        Intent intent = BodyEditActivity.newIntent(this, body.getUserId(), body.getId());
        startActivity(intent);
    }
}
