package com.brilltech.jelp.activities.dashboard;

import com.brilltech.jelp.api.Endpoints;
import com.brilltech.jelp.entities.EmergencyProto;
import com.brilltech.jelp.entities.JelpProto;
import com.brilltech.jelp.entities.ReqResProto;
import com.brilltech.jelp.utils.AppUtils;
import com.brilltech.jelp.utils.CallbackWrapper;

import retrofit2.Response;

public class DashBoardPresenterImpl implements DashBoardPresenter {
    private static final String TAG = "DashBoardPresenterImpl";
    private DashBoardActivity activity;
    private Endpoints endpoints;

    public DashBoardPresenterImpl(DashBoardActivity dashBoardActivity, Endpoints endpoints) {
        this.activity = dashBoardActivity;
        this.endpoints = endpoints;
    }

    @Override
    public void emergency(String token, JelpProto.RequestType helpType, double lat, double lng) {
        AppUtils.showLog(TAG, "lat: " + lat);
        AppUtils.showLog(TAG, "lng: " + lng);
        EmergencyProto.EmergencyRequest emergencyRequest = EmergencyProto.EmergencyRequest.newBuilder()
                .setLocationLat(lat)
                .setLocationLng(lng)
                .setRequestType(helpType)
                .build();

        endpoints.emergency(token, emergencyRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                AppUtils.showLog(TAG, "emergencyResponse: " + baseResponse);

                if (baseResponse == null) {
                    activity.onAssaultNotifyFail(null);
                    return;
                }

                if (baseResponse.getError()) {
                    activity.onAssaultNotifyFail(baseResponse.getMsg());
                    return;
                }

                activity.onAssaultNotifySuccess();

            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.onAssaultNotifyFail(null);
            }
        }));
    }

}
