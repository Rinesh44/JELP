package com.brilltech.jelp.activities.dashboard;

public interface DashBoardView {
    void onAssaultNotifySuccess();

    void onAssaultNotifyFail(String msg);

    void onMedicalNotifySuccess();

    void onMedicalNotifyFail(String msg);
}
