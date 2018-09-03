package com.github.dentou.fitnessassistant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import java.util.Locale;
import java.util.UUID;

public class BodyEditFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_BODY_ID = "body_id";

    private User mUser;
    private Body mBody;
    private TextInputLayout mBicepsTil;
    private TextInputLayout mTricepsTil;
    private TextInputLayout mSubscapularTil;
    private TextInputLayout mSuprailiacTil;
    private TextInputLayout mHeightTil;
    private TextInputLayout mWeightTil;

    private EditText mBicepsField;
    private EditText mTricepsField;
    private EditText mSubscapularField;
    private EditText mSuprailiacField;
    private EditText mHeightField;
    private EditText mWeightField;


    private boolean mSaveButtonEnabled = false;


    public static BodyEditFragment newInstance(UUID userId, UUID bodyId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_ID, userId);
        args.putSerializable(ARG_BODY_ID, bodyId);

        BodyEditFragment fragment = new BodyEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID userId = (UUID) getArguments().getSerializable(ARG_USER_ID);
        mUser = UserHandler.get(getActivity()).getUser(userId);

        UUID bodyId = (UUID) getArguments().getSerializable(ARG_BODY_ID);
        mBody = BodyHandler.get(getActivity()).getBody(userId, bodyId);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_edit, container, false);

        mBicepsTil = (TextInputLayout) view.findViewById(R.id.body_biceps_til);
        mBicepsField = (EditText) view.findViewById(R.id.body_biceps_edit);
        mBicepsField.addTextChangedListener(new BodyTextWatcher());

        mTricepsTil = (TextInputLayout) view.findViewById(R.id.body_triceps_til);
        mTricepsField = (EditText) view.findViewById(R.id.body_triceps_edit);
        mTricepsField.addTextChangedListener(new BodyTextWatcher());

        mSubscapularTil = (TextInputLayout) view.findViewById(R.id.body_subscapular_til);
        mSubscapularField = (EditText) view.findViewById(R.id.body_subscapular_edit);
        mSubscapularField.addTextChangedListener(new BodyTextWatcher());

        mSuprailiacTil = (TextInputLayout) view.findViewById(R.id.body_suprailiac_til);
        mSuprailiacField = (EditText) view.findViewById(R.id.body_suprailiac_edit);
        mSuprailiacField.addTextChangedListener(new BodyTextWatcher());

        mHeightTil = (TextInputLayout) view.findViewById(R.id.body_height_til);
        mHeightField = (EditText) view.findViewById(R.id.body_height_edit);
        mHeightField.addTextChangedListener(new BodyTextWatcher());

        mWeightTil = (TextInputLayout) view.findViewById(R.id.body_weight_til);
        mWeightField = (EditText) view.findViewById(R.id.body_weight_edit);
        mWeightField.addTextChangedListener(new BodyTextWatcher());

        updateInputFields();
        updateSaveButtonState();


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_edit, menu);

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
                updateBody();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateBody() {

        mBody.setBiceps(Integer.parseInt(mBicepsField.getText().toString()));
        mBody.setTriceps(Integer.parseInt(mTricepsField.getText().toString()));
        mBody.setSubscapular(Integer.parseInt(mSubscapularField.getText().toString()));
        mBody.setSuprailiac(Integer.parseInt(mSuprailiacField.getText().toString()));
        mBody.setHeight(Float.parseFloat(mHeightField.getText().toString()));
        mBody.setWeight(Float.parseFloat(mWeightField.getText().toString()));

        BodyHandler.get(getActivity()).updateBody(mBody);
    }

    private void updateInputFields() {

        mBicepsField.setText(getString(R.string.integer_format, mBody.getBiceps()));
        mTricepsField.setText(getString(R.string.integer_format, mBody.getTriceps()));
        mSubscapularField.setText(getString(R.string.integer_format, mBody.getSubscapular()));
        mSuprailiacField.setText(getString(R.string.integer_format, mBody.getSuprailiac()));
        mHeightField.setText(getString(R.string.float_format, mBody.getHeight()));
        mWeightField.setText(getString(R.string.float_format, mBody.getWeight()));

    }


    private void updateSaveButtonState() {
        mSaveButtonEnabled = validateInputFields();
        getActivity().invalidateOptionsMenu();
    }

    private boolean validateInputFields() {

        if (!(validateIntegerInputField(mBicepsField, mBicepsTil, R.string.body_measurements_error)
        && validateIntegerInputField(mTricepsField, mTricepsTil, R.string.body_measurements_error)
        && validateIntegerInputField(mSubscapularField, mSubscapularTil, R.string.body_measurements_error)
        && validateIntegerInputField(mSuprailiacField, mSuprailiacTil, R.string.body_measurements_error)
        && validateFloatInputField(mHeightField, mHeightTil, R.string.body_measurements_error)
        && validateFloatInputField(mWeightField, mWeightTil, R.string.body_measurements_error)
        )) {

            return false;
        }

        return true;
    }


    private boolean validateFloatInputField(EditText inputField, TextInputLayout til, int errorResId) {

        til.setError(getString(errorResId));
        if (inputField.getText().length() == 0 ) {
            return false;
        }
        float value = 0;
        try {
            value = Float.parseFloat(inputField.getText().toString());
        } catch (NumberFormatException e) {
            return false;
        }
        if (value <= 0) {
            return false;
        }
        til.setError(null);
        return true;
    }

    private boolean validateIntegerInputField(EditText inputField, TextInputLayout til, int errorResId) {

        til.setError(getString(errorResId));
        if (inputField.getText().length() == 0 ) {
            return false;
        }
        int value = 0;
        try {
            value = Integer.parseInt(inputField.getText().toString());
        } catch (NumberFormatException e) {
            return false;
        }
        if (value <= 0) {
            return false;
        }
        til.setError(null);
        return true;
    }

    private class BodyTextWatcher implements TextWatcher {
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
    }
}
