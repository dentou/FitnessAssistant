package com.github.dentou.fitnessassistant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.github.dentou.fitnessassistant.model.Body;
import com.github.dentou.fitnessassistant.model.User;
import com.github.dentou.fitnessassistant.worker.BodyHandler;
import com.github.dentou.fitnessassistant.worker.UserHandler;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.UUID;

public class BodyEditFragment extends Fragment {

    private static final String TAG = "BodyEditFragment";

    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_BODY_ID = "body_id";

    private User mUser;
    private Body mLastestBody;
    private Body mBody;


    private CheckBox mUseLastValuesCheckbox;

    private EditText mBicepsField;
    private EditText mTricepsField;
    private EditText mSubscapularField;
    private EditText mSuprailiacField;
    private EditText mHeightField;
    private EditText mWeightField;


    private boolean mSaveButtonEnabled = false;
    private boolean mUseLastValues = false;


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

        mLastestBody = BodyHandler.get(getActivity()).getLatestBody(userId);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_edit, container, false);

        mBicepsField = (EditText) view.findViewById(R.id.body_biceps_edit);
        mBicepsField.addTextChangedListener(new BodyTextWatcher());

        mTricepsField = (EditText) view.findViewById(R.id.body_triceps_edit);
        mTricepsField.addTextChangedListener(new BodyTextWatcher());

        mSubscapularField = (EditText) view.findViewById(R.id.body_subscapular_edit);
        mSubscapularField.addTextChangedListener(new BodyTextWatcher());

        mSuprailiacField = (EditText) view.findViewById(R.id.body_suprailiac_edit);
        mSuprailiacField.addTextChangedListener(new BodyTextWatcher());

        mHeightField = (EditText) view.findViewById(R.id.body_height_edit);
        mHeightField.addTextChangedListener(new BodyTextWatcher());

        mWeightField = (EditText) view.findViewById(R.id.body_weight_edit);
        mWeightField.addTextChangedListener(new BodyTextWatcher());

        mUseLastValuesCheckbox = (CheckBox) view.findViewById(R.id.body_use_last_value_checkbox);
        mUseLastValuesCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mUseLastValues = isChecked;
                updateInputFields();
            }
        });

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

        try {
            NumberFormat nfi = NumberFormat.getIntegerInstance();
            NumberFormat nf = NumberFormat.getInstance();
            mBody.setBiceps(nfi.parse(mBicepsField.getText().toString()).intValue());
            mBody.setTriceps(nfi.parse(mTricepsField.getText().toString()).intValue());
            mBody.setSubscapular(nfi.parse(mSubscapularField.getText().toString()).intValue());
            mBody.setSuprailiac(nfi.parse(mSuprailiacField.getText().toString()).intValue());
            mBody.setHeight(nf.parse(mHeightField.getText().toString()).floatValue());
            mBody.setWeight(nf.parse(mWeightField.getText().toString()).floatValue());
        } catch (ParseException e) {
            Log.e(TAG, "Unable to update body", e);
        }


        BodyHandler.get(getActivity()).updateBody(mBody);
    }

    private void updateInputFields() {

        if (mUseLastValues) {
            if (mLastestBody != null) {
                mBody.setHeight(mLastestBody.getHeight());
                mBody.setWeight(mLastestBody.getWeight());
            }
        }

        mHeightField.setEnabled(!mUseLastValues);
        mWeightField.setEnabled(!mUseLastValues);

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

        if (!(validateIntegerInputField(mBicepsField, R.string.body_measurements_error)
        && validateIntegerInputField(mTricepsField, R.string.body_measurements_error)
        && validateIntegerInputField(mSubscapularField, R.string.body_measurements_error)
        && validateIntegerInputField(mSuprailiacField, R.string.body_measurements_error)
        && validateFloatInputField(mHeightField, R.string.body_measurements_error)
        && validateFloatInputField(mWeightField, R.string.body_measurements_error)
        )) {

            return false;
        }

        return true;
    }


    private boolean validateFloatInputField(EditText inputField, int errorResId) {

        inputField.setError(getString(errorResId));
        if (inputField.getText().length() == 0 ) {
            return false;
        }
        float value = 0;
        try {
            value = NumberFormat.getNumberInstance().parse(inputField.getText().toString()).floatValue();
        } catch (ParseException e) {
            return false;
        }
        if (value <= 0) {
            return false;
        }
        inputField.setError(null);
        return true;
    }

    private boolean validateIntegerInputField(EditText inputField, int errorResId) {

        inputField.setError(getString(errorResId));
        if (inputField.getText().length() == 0 ) {
            return false;
        }
        int value = 0;
        try {
            value = NumberFormat.getIntegerInstance().parse(inputField.getText().toString()).intValue();
        } catch (ParseException e) {
            return false;
        }
        if (value <= 0) {
            return false;
        }
        inputField.setError(null);
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
