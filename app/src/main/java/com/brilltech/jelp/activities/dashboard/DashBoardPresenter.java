package com.brilltech.jelp.activities.dashboard;

import com.brilltech.jelp.entities.JelpProto;

public interface DashBoardPresenter {
    void emergency(String token, JelpProto.RequestType helpType, double lat, double lng);
}
