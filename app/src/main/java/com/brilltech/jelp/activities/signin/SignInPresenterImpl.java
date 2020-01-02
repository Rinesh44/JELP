package com.brilltech.jelp.activities.signin;

import com.brilltech.jelp.api.Endpoints;
import com.brilltech.jelp.entities.AuthProto;
import com.brilltech.jelp.entities.JelpProto;
import com.brilltech.jelp.entities.ReqResProto;
import com.brilltech.jelp.utils.AppUtils;
import com.brilltech.jelp.utils.CallbackWrapper;

import retrofit2.Response;

public class SignInPresenterImpl implements SignInPresenter {
    private static final String TAG = "SignInPresenterImpl";
    private SignInActivity activity;
    private Endpoints endpoints;

    public SignInPresenterImpl(SignInActivity activity, Endpoints endpoints) {
        this.activity = activity;
        this.endpoints = endpoints;
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
