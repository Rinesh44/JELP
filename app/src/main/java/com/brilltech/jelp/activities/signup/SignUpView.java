package com.brilltech.jelp.activities.signup;

import com.brilltech.jelp.entities.ReqResProto;

public interface SignUpView {
    void onSignUpSuccess(ReqResProto.Response response, String email, String password);

    void onSignUpFail(String msg);

    void onSignInSuccess(ReqResProto.Response response);

    void onSignInFail(String msg);
}
