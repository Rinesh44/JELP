package com.brilltech.jelp.activities.signin;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.brilltech.jelp.R;
import com.brilltech.jelp.activities.base.BaseActivity;
import com.brilltech.jelp.activities.dashboard.DashBoardActivity;
import com.brilltech.jelp.activities.signup.SignUpActivity;
import com.brilltech.jelp.api.Endpoints;
import com.brilltech.jelp.entities.ReqResProto;
import com.brilltech.jelp.utils.AppUtils;
import com.brilltech.jelp.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.brilltech.jelp.JelpApp.getFirebaseToken;
import static com.brilltech.jelp.JelpApp.getMyApplication;

public class SignInActivity extends BaseActivity implements SignInView {
    private static final String TAG = "SignInActivity";
    public static final int PERMISSIONS_CODE = 111;
    @Inject
    Endpoints endpoints;
    @BindView(R.id.tv_sign_up)
    TextView mSignUp;
    @BindView(R.id.il_email)
    TextInputLayout mEmailLayout;
    @BindView(R.id.il_password)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.et_email)
    TextInputEditText mEmail;
    @BindView(R.id.et_password)
    TextInputEditText mPassword;
    @BindView(R.id.btn_sign_in)
    MaterialButton mSignIn;
    @BindView(R.id.cb_remember_me)
    CheckBox mRememberMe;

    private SignInPresenter presenter;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);
        initialize();
        presenter = new SignInPresenterImpl(this, endpoints);


        checkRequiredPermissions();
        Typeface typeface = ResourcesCompat.getFont(this, R.font.proxima_regular);
        mPasswordLayout.setTypeface(typeface);

        gotoDashBoardIfLoggedIn();
        setEmailIfRemembered();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        mRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    editor.putBoolean(Constants.REMEMBER_ME, true);
                    editor.apply();
                } else {
                    editor.putBoolean(Constants.REMEMBER_ME, false);
                    editor.apply();
                }
            }
        });


        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail.getText().toString().isEmpty()) {
                    mEmailLayout.requestFocus();
                    mEmailLayout.setErrorEnabled(true);
                    mEmailLayout.setError("This field is required");
                    return;
                } else {
                    mEmailLayout.setErrorEnabled(false);
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
                    mEmailLayout.requestFocus();
                    mEmailLayout.setErrorEnabled(true);
                    mEmailLayout.setError("Invalid email address");
                    return;
                } else {
                    mEmailLayout.setErrorEnabled(false);
                }

                if (mPassword.getText().toString().isEmpty()) {
                    mPasswordLayout.requestFocus();
                    mPasswordLayout.setErrorEnabled(true);
                    mPasswordLayout.setError("This field is required");
                    return;
                } else {
                    mPasswordLayout.setErrorEnabled(false);
                }

                showLoading();
                String pushToken = getFirebaseToken();
                AppUtils.showLog(TAG, "firebase token: " + pushToken);
                presenter.signIn(mEmail.getText().toString().trim(), mPassword.getText().toString().trim(),
                        pushToken);

            }
        });
    }

    private void gotoDashBoardIfLoggedIn() {
        if (preferences.getBoolean(Constants.LOGGED_IN, false)) {
            Intent i = new Intent(SignInActivity.this, DashBoardActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    private void setEmailIfRemembered() {
        AppUtils.showLog(TAG, "setEmailIfRememebered()");
        if (preferences.getBoolean(Constants.REMEMBER_ME, false)) {
            AppUtils.showLog(TAG, "remember me true");
            String email = preferences.getString(Constants.LOGGED_IN_EMAIL, "");
            if (!email.isEmpty()) mEmail.setText(email);
            else AppUtils.showLog(TAG, "email empty");
        }
    }

    private void initialize() {
        getMyApplication(this).getAppComponent().inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

    @Override
    public void onSignInSuccess(ReqResProto.Response authBaseResponse) {
        AppUtils.showLog(TAG, "login success");

        saveUserDataToPreference(authBaseResponse);

        Intent i = new Intent(SignInActivity.this, DashBoardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    @Override
    public void onSignInFail(String msg) {
        showMessage(msg);
    }

    private void checkRequiredPermissions() {
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                && hasPermission((Manifest.permission.WRITE_EXTERNAL_STORAGE))
                && hasPermission(Manifest.permission.CAMERA) &&
                hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return;
        }

        requestPermissionsSafely(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                , PERMISSIONS_CODE);
    }
}
