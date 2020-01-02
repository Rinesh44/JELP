package com.brilltech.jelp.activities.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.brilltech.jelp.R;
import com.brilltech.jelp.activities.base.BaseActivity;
import com.brilltech.jelp.activities.dashboard.DashBoardActivity;
import com.brilltech.jelp.api.Endpoints;
import com.brilltech.jelp.entities.ReqResProto;
import com.brilltech.jelp.utils.AppUtils;
import com.brilltech.jelp.utils.Constants;
import com.brilltech.jelp.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.brilltech.jelp.JelpApp.getFirebaseToken;
import static com.brilltech.jelp.JelpApp.getMyApplication;

public class SignUpActivity extends BaseActivity implements SignUpView {
    private static final String TAG = "SignUpActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.il_full_name)
    TextInputLayout mFullNameLayout;
    @BindView(R.id.et_full_name)
    TextInputEditText mFullName;
    @BindView(R.id.il_username)
    TextInputLayout mEmailLayout;
    @BindView(R.id.et_username)
    TextInputEditText mEmail;
    @BindView(R.id.il_password)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.et_password)
    TextInputEditText mPassword;
    @BindView(R.id.btn_sign_up)
    MaterialButton mSignUp;
    @BindView(R.id.cb_tnc)
    CheckBox mTermsAndConditions;
    @BindView(R.id.country_picker)
    CountryCodePicker mCountryPicker;
    @BindView(R.id.il_address)
    TextInputLayout mAddressLayout;
    @BindView(R.id.et_address)
    TextInputEditText mAddress;
    /*   @BindView(R.id.il_phone)
       TextInputLayout mPhoneLayout;*/
/*    @BindView(R.id.et_phone)
    TextInputEditText mPhone;*/
    @BindView(R.id.sp_gender)
    Spinner mGender;

    private SignUpPresenter presenter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.proxima_regular);
        mPasswordLayout.setTypeface(typeface);


        getMyApplication(this).getAppComponent().inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        presenter = new SignUpPresenterImpl(this, endpoints);
        mCountryPicker.setCountryForNameCode("US");

        setUpGenderSpinner();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFullName.getText().toString().isEmpty()) {
                    mFullNameLayout.requestFocus();
                    mFullNameLayout.setErrorEnabled(true);
                    mFullNameLayout.setError("This field is required");
                    return;
                } else {
                    mFullNameLayout.setErrorEnabled(false);
                }

                if (mAddress.getText().toString().isEmpty()) {
                    mAddressLayout.requestFocus();
                    mAddressLayout.setErrorEnabled(true);
                    mAddressLayout.setError("This field is required");
                    return;
                } else {
                    mAddressLayout.setErrorEnabled(false);
                }

        /*        if (mPhone.getText().toString().isEmpty()) {
                    mPhoneLayout.requestFocus();
                    mPhoneLayout.setErrorEnabled(true);
                    mPhoneLayout.setError("This field is required");
                    return;
                } else {
                    mPhoneLayout.setErrorEnabled(false);
                }*/

                if (mEmail.getText().toString().isEmpty()) {
                    mEmailLayout.requestFocus();
                    mEmailLayout.setErrorEnabled(true);
                    mEmailLayout.setError("This field is required");
                    return;
                } else {
                    mEmailLayout.setErrorEnabled(false);
                }

                if (ValidationUtils.isValidEmailPhone(mEmail.getText().toString().trim())) {
                    mEmailLayout.requestFocus();
                    mEmailLayout.setErrorEnabled(true);
                    mEmailLayout.setError("Invalid email/phone");
                    return;
                } else mEmailLayout.setErrorEnabled(false);

                if (mPassword.getText().toString().isEmpty()) {
                    mPasswordLayout.requestFocus();
                    mPasswordLayout.setErrorEnabled(true);
                    mPasswordLayout.setError("This field is required");
                    return;
                } else {
                    mPasswordLayout.setErrorEnabled(false);
                }

                if (!mTermsAndConditions.isChecked()) {
                    Toast.makeText(SignUpActivity.this, "Please accept our terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }


                showLoading();
                presenter.signUp(mFullName.getText().toString().trim(), mAddress.getText().toString().trim()
                        , mCountryPicker.getSelectedCountryName(),
                        mGender.getSelectedItem().toString().trim(),
                        mEmail.getText().toString().trim(), mPassword.getText().toString().trim());
            }
        });

    }

    private void setUpGenderSpinner() {
        String[] items = new String[]{"Male", "Female", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mGender.setAdapter(adapter);
    }

    @Override
    public void onSignUpSuccess(ReqResProto.Response response, String email, String password) {
        AppUtils.showLog(TAG, "sign up success");
        presenter.signIn(email, password, getFirebaseToken());
    }

    @Override
    public void onSignUpFail(String msg) {
        showMessage(msg);
    }

    @Override
    public void onSignInSuccess(ReqResProto.Response response) {
        AppUtils.showLog(TAG, "sign in success");
        saveUserDataToPreference(response);

        Intent i = new Intent(SignUpActivity.this, DashBoardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onSignInFail(String msg) {
        showMessage(msg);
    }

    private void saveUserDataToPreference(ReqResProto.Response signUpResponse) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.LOGGED_IN_USER_FULL_NAME, signUpResponse.getUser().getFullName());
        editor.putString(Constants.LOGGED_IN_EMAIL, signUpResponse.getUser().getEmailPhone());
        editor.putString(Constants.COUNTRY, signUpResponse.getUser().getCountry());
        editor.putString(Constants.ADDRESS, signUpResponse.getUser().getAddress());
        editor.putString(Constants.LOGGED_IN_USER_ID, signUpResponse.getUser().getUserId());
        editor.putInt(Constants.STATUS_CODE, signUpResponse.getUser().getUserStatus().getNumber());
        editor.putBoolean(Constants.LOGGED_IN, true);
        editor.putString(Constants.TOKEN, signUpResponse.getLoginResponse().getToken());
        editor.apply();
    }
}
