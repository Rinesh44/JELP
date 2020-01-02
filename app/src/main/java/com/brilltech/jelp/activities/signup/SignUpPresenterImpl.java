package com.brilltech.jelp.activities.signup;

import com.brilltech.jelp.api.Endpoints;
import com.brilltech.jelp.entities.AccountProto;
import com.brilltech.jelp.entities.AuthProto;
import com.brilltech.jelp.entities.JelpProto;
import com.brilltech.jelp.entities.ReqResProto;
import com.brilltech.jelp.entities.UserProto;
import com.brilltech.jelp.utils.AppUtils;
import com.brilltech.jelp.utils.CallbackWrapper;

import retrofit2.Response;

public class SignUpPresenterImpl implements SignUpPresenter {
    private static final String TAG = "SignUpPresenterImpl";
    private SignUpActivity activity;
    private Endpoints endpoints;

    public SignUpPresenterImpl(SignUpActivity activity, Endpoints endpoints) {
        this.activity = activity;
        this.endpoints = endpoints;
    }

    @Override
    public void signUp(String fullname, String address, String country, String gender, final String email, final String password) {
        JelpProto.Gender selectedGender = JelpProto.Gender.UNRECOGNIZED;
        switch (gender) {
            case "Male":
                selectedGender = JelpProto.Gender.MALE;
                break;

            case "Female":
                selectedGender = JelpProto.Gender.FEMALE;
                break;

            case "Others":
                selectedGender = JelpProto.Gender.UNKNOWN_GENDER;
                break;
        }

        UserProto.User user = UserProto.User.newBuilder()
                .setFullName(fullname)
                .setEmailPhone(email)
                .setAddress(address)
                .setCountry(country)
                .setGender(selectedGender)
                .build();

        AccountProto.AddUserRequest addUserRequest = AccountProto.AddUserRequest.newBuilder()
                .setUser(user)
                .setPassword(password)
//                .setPassword(ByteString.copyFrom(password, "UTF-8"))
                .build();

        endpoints.signUp(addUserRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();
                AppUtils.showLog(TAG, "signUpResponse: " + baseResponse);

                if (baseResponse == null) {
                    activity.onSignUpFail(null);
                    return;
                }

                if (baseResponse.getError()) {
                    activity.onSignUpFail(baseResponse.getMsg());
                    return;
                }

                activity.onSignUpSuccess(baseResponse, email, password);

            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.onSignUpFail(null);
            }
        }));
    }

    @Override
    public void signIn(String emailPhone, String password, String firebaseToken) {
        AuthProto.LoginRequest loginRequest = AuthProto.LoginRequest.newBuilder()
                .setEmailPhone(emailPhone)
//                .setPassword(ByteString.copyFrom(password, "UTF-8"))
                .setPassword(password)
                .setPushToken(firebaseToken)
                .setDeviceType(JelpProto.DeviceType.ANDROID)
                .build();


        endpoints.signIn(loginRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();
                AppUtils.showLog(TAG, "loginResponse: " + baseResponse);

                if (baseResponse == null) {
                    activity.onSignInFail(null);
                    return;
                }

                if (baseResponse.getError()) {
                    activity.onSignInFail(baseResponse.getMsg());
                    return;
                }

                activity.onSignInSuccess(baseResponse);

            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.onSignInFail(null);
            }
        }));
    }
}
