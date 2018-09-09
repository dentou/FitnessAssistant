package com.github.dentou.fitnessassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.dentou.fitnessassistant.model.User;
import com.github.dentou.fitnessassistant.worker.UserHandler;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String ARG_USER_ID = "user_id";

    private User mUser;
    private FloatingActionButton mFab;
    private TextView mNameView;
    private TextView mGenderView;
    private TextView mDateView;

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

        mFab = (FloatingActionButton) view.findViewById(R.id.profile_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.onProfileEdited(mUser);
            }
        });

        mNameView = (TextView) view.findViewById(R.id.profile_name_view);

        mGenderView = (TextView) view.findViewById(R.id.profile_gender_view);


        mDateView = (TextView) view.findViewById(R.id.profile_date_view);


        updateUI();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    private void updateUI() {
        mUser = UserHandler.get(getActivity()).getUser(mUser.getId());

        mNameView.setText(mUser.getName());
        mGenderView.setText(mUser.getGender() == User.MALE ? R.string.gender_male : R.string.gender_female);
        mDateView.setText(new SimpleDateFormat("EEE, d MMM yyyy", Locale.US).format(mUser.getDateOfBirth()));
    }
}
