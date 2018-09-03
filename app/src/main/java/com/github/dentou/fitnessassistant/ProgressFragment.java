package com.github.dentou.fitnessassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

public class ProgressFragment extends Fragment {

    public static final String TAG = "ProgressFragment";
    private static final String ARG_USER_ID = "user_id";

    private User mUser;
    private Callbacks mCallbacks;
    private FloatingActionButton mFab;

    public static ProgressFragment newInstance(UUID userId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, userId);

        ProgressFragment fragment = new ProgressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onBodyCreated(Body body);
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

        Log.i(TAG, "Bodies = " + BodyHandler.get(getActivity()).getBodies(userId).size());

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        mFab = (FloatingActionButton) view.findViewById(R.id.progress_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Body body = new Body(mUser.getId());
                Log.i(TAG, "New body created for user " + mUser.getName() + " with id " + body.getId());
                BodyHandler.get(getActivity()).addBody(body);
                mCallbacks.onBodyCreated(body);
            }
        });

        updateUI();

        return view;
    }

    private void updateUI() {
        mUser = UserHandler.get(getActivity()).getUser(mUser.getId());

        // todo
    }
}
