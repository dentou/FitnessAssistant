package com.github.dentou.fitnessassistant;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ProfileEditFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private User mUser;
    private EditText mNameField;
    private Button mDateButton;
    private EditText mHeightField;
    private EditText mWeightField;
    private TextInputLayout mNameTil;
    private TextInputLayout mHeightTil;
    private TextInputLayout mWeightTil;

    private boolean mSaveButtonEnabled = false;

    public static ProfileEditFragment newInstance(UUID userId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, userId);

        ProfileEditFragment fragment = new ProfileEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID userId = (UUID) getArguments().getSerializable(ARG_USER_ID);
        mUser = UserHandler.get(getActivity()).getUser(userId);
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        UserHandler.get(getActivity()).updateUser(mUser);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        mNameTil = (TextInputLayout) view.findViewById(R.id.profile_name_til);

        mNameField = (EditText) view.findViewById(R.id.profile_name_edit);
        mNameField.setText(mUser.getName());
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    mNameTil.setError(getString(R.string.profile_name_error));
                    return;
                }

                mNameTil.setError(null);
                mUser.setName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateSaveButtonState();
            }
        });

        mDateButton = (Button) view.findViewById(R.id.profile_date_button);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mUser.getDateOfBirth());
                dialog.setTargetFragment(ProfileEditFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mHeightTil = (TextInputLayout) view.findViewById(R.id.profile_height_til);

        mHeightField = (EditText) view.findViewById(R.id.profile_height_edit);
        mHeightField.setText(String.format(Locale.US, "%.2f", mUser.getHeight()));
        mHeightField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0 ) {
                    mHeightTil.setError(getString(R.string.profile_height_error));
                    return;
                }
                float height = Float.parseFloat(charSequence.toString());
                if (height <= 0) {
                    mHeightTil.setError(getString(R.string.profile_height_error));
                    return;
                }
                mHeightTil.setError(null);
                mUser.setHeight(height);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateSaveButtonState();
            }
        });

        mWeightTil = (TextInputLayout) view.findViewById(R.id.profile_weight_til);

        mWeightField = (EditText) view.findViewById(R.id.profile_weight_edit);
        mWeightField.setText(String.format(Locale.US, "%.2f", mUser.getWeight()));
        mWeightField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    mWeightTil.setError(getString(R.string.profile_weight_error));
                    return;
                }
                float weight = Float.parseFloat(charSequence.toString());
                if (weight <= 0) {
                    mWeightTil.setError(getString(R.string.profile_weight_error));
                    return;
                }
                mWeightTil.setError(null);
                mUser.setWeight(weight);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateSaveButtonState();
            }
        });


        updateSaveButtonState();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mUser.setDateOfBirth(date);
            updateDate();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_profile_edit, menu);

        MenuItem saveButton = (MenuItem) menu.findItem(R.id.profile_save);
        if (mSaveButtonEnabled) {
            saveButton.setEnabled(true);
            saveButton.getIcon().setAlpha(255);
        } else {
            saveButton.setEnabled(false);
            saveButton.getIcon().setAlpha(130);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_save:
                updateUser();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateUser() {
        UserHandler.get(getActivity()).updateUser(mUser);
    }

    private void updateDate() {
        mDateButton.setText(new SimpleDateFormat("EEE, d MMM yyyy", Locale.US).format(mUser.getDateOfBirth()));
    }

    private void updateSaveButtonState() {
        if (mNameField.getText().length() != 0
            && mHeightField.getText().length() != 0
            && mWeightField.getText().length() != 0) {

            if (mSaveButtonEnabled) {
                return;
            }
            mSaveButtonEnabled = true;
        } else {
            if (!mSaveButtonEnabled) {
                return;
            }
            mSaveButtonEnabled = false;
        }
        getActivity().invalidateOptionsMenu();
    }

}
