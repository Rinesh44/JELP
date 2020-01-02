package com.brilltech.jelp.component;


import com.brilltech.jelp.activities.dashboard.DashBoardActivity;
import com.brilltech.jelp.activities.signin.SignInActivity;
import com.brilltech.jelp.activities.signup.SignUpActivity;
import com.brilltech.jelp.component.module.AppModule;
import com.brilltech.jelp.component.module.NetModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {
    void inject(SignUpActivity loginActivity);

    void inject(SignInActivity signInActivity);

    void inject(DashBoardActivity dashboardActivity);
}