package com.brilltech.jelp.activities.signin;

import com.brilltech.jelp.entities.ReqResProto;

public interface SignInView {
    void onSignInSuccess(ReqResProto.Response response);

    void onSignInFail(String msg);
}
