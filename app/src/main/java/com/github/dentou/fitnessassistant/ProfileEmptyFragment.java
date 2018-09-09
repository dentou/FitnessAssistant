package com.github.dentou.fitnessassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.dentou.fitnessassistant.model.User;
import com.github.dentou.fitnessassistant.worker.UserHandler;

public class ProfileEmptyFragment extends Fragment {

    private static final String TAG = "ProfileEmptyFragment";

    private static final int REQUEST_PROFILE = 0;

    private Button mCreateButton;
    private Callbacks mCallbacks;


    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onProfileCreated(User user);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_empty, container, false);

        mCreateButton = (Button) view.findViewById(R.id.create_profile_button);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                Log.i(TAG, "New user created with id" + user.getId().toString());
                UserHandler.get(getActivity()).addUser(user);
                mCallbacks.onProfileCreated(user);
            }
        });


        return view;
    }
}
