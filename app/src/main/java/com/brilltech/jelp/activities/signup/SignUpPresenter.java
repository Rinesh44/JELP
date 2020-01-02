package com.brilltech.jelp.activities.signup;

public interface SignUpPresenter {
    void signUp(String fullname, String address, String country, String gender,
                String email, String password);

    void signIn(String emailPhone, String password, String firebaseToken);
}
