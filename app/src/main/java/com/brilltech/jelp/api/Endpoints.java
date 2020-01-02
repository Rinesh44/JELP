package com.brilltech.jelp.api;

import com.brilltech.jelp.entities.AccountProto;
import com.brilltech.jelp.entities.AuthProto;
import com.brilltech.jelp.entities.EmergencyProto;
import com.brilltech.jelp.entities.ReqResProto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Endpoints {

    String CONTENT_TYPE = "Content-Type: application/protobuf";
    String AUTHORIZATION = "authorization";

    String API_BASE_URL = "http://192.168.0.112:9000";
    String LOGIN = "/login";
    String SIGN_UP = "/user";
    String LOGOUT = "/logout";
    String EMERGENCY = "/emergency/request";
    String GET_PROFILE = "/user/{userId}";

    @Headers({CONTENT_TYPE})
    @POST(API_BASE_URL + LOGIN)
    Call<ReqResProto.Response> signIn(@Body AuthProto.LoginRequest loginRequest);

    @Headers({CONTENT_TYPE})
    @POST(API_BASE_URL + SIGN_UP)
    Call<ReqResProto.Response> signUp(@Body AccountProto.AddUserRequest addUserRequest);

    @GET(API_BASE_URL + LOGOUT)
    Call<ReqResProto.Response> logout(@Header(AUTHORIZATION) String authorization);

    @POST(API_BASE_URL + EMERGENCY)
    Call<ReqResProto.Response> emergency(@Header(AUTHORIZATION) String authorization,
                                         @Body EmergencyProto.EmergencyRequest emergencyRequest);

    @GET(API_BASE_URL + GET_PROFILE)
    Call<ReqResProto.Response> getProfile(@Header(AUTHORIZATION) String authorization,
                                          @Path(value = "userId") String userId);

}
