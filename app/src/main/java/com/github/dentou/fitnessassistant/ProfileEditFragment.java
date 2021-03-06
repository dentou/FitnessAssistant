package com.github.dentou.fitnessassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.github.dentou.fitnessassistant.model.User;
import com.github.dentou.fitnessassistant.worker.UserHandler;

import java.util.Date;
import java.util.UUID;

public class ProfileEditFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private User mUser;
    private EditText mNameField;
    private RadioGroup mGenderRadio;
    private Button mDateButton;


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

        mNameField = (EditText) view.findViewById(R.id.profile_name_edit);
        mNameField.setText(mUser.getName());
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateSaveButtonState();
            }
        });

        mGenderRadio = (RadioGroup) view.findViewById(R.id.profile_gender);
        mGenderRadio.check(mUser.getGender() == User.MALE ? R.id.profile_male : R.id.profile_female);


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
        inflater.inflate(R.menu.fragment_edit, menu);

        MenuItem saveButton = (MenuItem) menu.findItem(R.id.menu_save);
        if (mSaveButtonEnabled) {
            saveButton.setEnabled(true);
            saveButton.getIcon().setAlpha(255);
        } else {
            saveButton.setEnabled(false);
            saveButton.getIcon().setAlpha(130);
        }

        MenuItem deleteButton = (MenuItem) menu.findItem(R.id.menu_delete);
        deleteButton.setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                updateUser();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateUser() {
        mUser.setName(mNameField.getText().toString());
        mUser.setGender(mGenderRadio.getCheckedRadioButtonId() == R.id.profile_male ? User.MALE : User.FEMALE);

        UserHandler.get(getActivity()).updateUser(mUser);
    }

    private void updateDate() {
        mDateButton.setText(DateUtils.formatDateTime(
                getActivity(), mUser.getDateOfBirth().getTime(),
                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_ALL));
    }

    private void updateSaveButtonState() {
        mSaveButtonEnabled = validateInputFields();
        getActivity().invalidateOptionsMenu();
    }

    private boolean validateInputFields() {
        if (mNameField.getText().length() == 0) {
            mNameField.setError(getString(R.string.profile_name_error));
            return false;
        } else {
            mNameField.setError(null);
        }

        return true;
    }





}
