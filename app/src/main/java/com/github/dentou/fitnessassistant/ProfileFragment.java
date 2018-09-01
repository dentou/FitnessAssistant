package com.github.dentou.fitnessassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String ARG_USER_ID = "user_id";

    private User mUser;
    private TextView mNameView;
    private TextView mDateView;
    private TextView mHeightView;
    private TextView mWeightView;
    private Callbacks mCallbacks;

    public static ProfileFragment newInstance(UUID userId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, userId);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onProfileEdited(User user);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID userId = (UUID) getArguments().getSerializable(ARG_USER_ID);
        mUser = UserHandler.get(getActivity()).getUser(userId);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mNameView = (TextView) view.findViewById(R.id.profile_name_view);

        mDateView = (TextView) view.findViewById(R.id.profile_date_view);

        mHeightView = (TextView) view.findViewById(R.id.profile_height_view);

        mWeightView = (TextView) view.findViewById(R.id.profile_weight_view);

        updateUI();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_edit_menu:
                mCallbacks.onProfileEdited(mUser);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        mUser = UserHandler.get(getActivity()).getUser(mUser.getId());

        mNameView.setText(mUser.getName());
        mDateView.setText(new SimpleDateFormat("EEE, d MMM yyyy", Locale.US).format(mUser.getDateOfBirth()));
        mHeightView.setText(String.format(Locale.US, "%.2f", mUser.getHeight()));
        mWeightView.setText(String.format(Locale.US, "%.2f", mUser.getWeight()));
    }
}
