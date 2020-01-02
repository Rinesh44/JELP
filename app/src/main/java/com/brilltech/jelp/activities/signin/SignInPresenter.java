package com.brilltech.jelp.activities.signin;

public interface SignInPresenter {
    void signIn(String emailPhone, String password, String firebaseToken);
}
