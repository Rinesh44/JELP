package com.brilltech.jelp;

import android.app.Application;
import android.content.Context;

import com.brilltech.jelp.component.AppComponent;
import com.brilltech.jelp.component.DaggerAppComponent;
import com.brilltech.jelp.component.module.AppModule;
import com.brilltech.jelp.component.module.NetModule;
import com.brilltech.jelp.utils.AppUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class JelpApp extends Application {
    private static final String TAG = "RccsApp";
    private AppComponent appComponent;
    public static String refreshedToken;

    @Override
    public void onCreate() {
        super.onCreate();
        setFirebaseToken();
    }

    public static JelpApp getMyApplication(Context context) {
        return (JelpApp) context.getApplicationContext();
    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .netModule(new NetModule())
                    .build();
        }
        return appComponent;
    }

    public void setFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                refreshedToken = instanceIdResult.getToken();
                AppUtils.showLog(TAG, "refreshed token : " + refreshedToken);
            }
        });
    }

    public static String getFirebaseToken() {
        return refreshedToken;
    }

}
