package com.github.dentou.fitnessassistant;

import android.content.Intent;
import android.os.Bundle;
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
        implements ProfileEmptyFragment.Callbacks, ProfileFragment.Callbacks {

//    private DrawerLayout mDrawerLayout;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<User> users = UserHandler.get(this).getUsers();
        if (users != null && !users.isEmpty()) {
            mUser = users.get(0);
        }

        PrimaryDrawerItem profileDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_profile).withIcon(GoogleMaterial.Icon.gmd_person);
        PrimaryDrawerItem reportsDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_reports).withIcon(GoogleMaterial.Icon.gmd_equalizer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(GoogleMaterial.Icon.gmd_person)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        profileDrawerItem,
                        new DividerDrawerItem(),
                        reportsDrawerItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return true;
                    }
                })
                .build();


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            if (mUser == null) {
                fragment = new ProfileEmptyFragment();
            } else {
                fragment = ProfileFragment.newInstance(mUser.getId());
            }
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }

    @Override
    public void onProfileCreated(User user) {
        Fragment fragment = ProfileFragment.newInstance(user.getId());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        Intent intent = ProfileEditActivity.newIntent(this, user.getId());
        startActivity(intent);
    }

    @Override
    public void onProfileEdited(User user) {
        Intent intent = ProfileEditActivity.newIntent(this, user.getId());
        startActivity(intent);
    }
}
