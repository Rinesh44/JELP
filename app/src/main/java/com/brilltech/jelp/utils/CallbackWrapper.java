package com.brilltech.jelp.utils;


import androidx.annotation.NonNull;

import com.brilltech.jelp.activities.base.MvpView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class CallbackWrapper<T> implements Callback<T> {
    private static final String TAG = "CallbackWrapper";
    private Wrapper<T> wrapper;
    private MvpView mvpView;

    public CallbackWrapper(MvpView view, Wrapper<T> wrapper) {
        this.wrapper = wrapper;
        this.mvpView = view;
    }

    /**
     * Invoked for a received HTTP response.
     * <p>
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call {@link Response#isSuccessful()} to determine if the response indicates success.
     */
    public void onResponse(@NonNull Call<T> call,
                           @NonNull Response<T> response) {

        if (response.isSuccessful()) {
            wrapper.onSuccessResult(response);
            return;
        }


        switch (response.code()) {
            case HttpsURLConnection.HTTP_UNAUTHORIZED:
                AppUtils.showLog(TAG, "Unauthorized");
            case HttpsURLConnection.HTTP_FORBIDDEN:
                AppUtils.showLog(TAG, "Forbidden");
            case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                mvpView.showMessage("Some exception occurred in server");
            case HttpsURLConnection.HTTP_NOT_FOUND:
                wrapper.onFailureResult();
//                mvpView.showMessage("Not found");
            default:
                mvpView.showMessage(null);
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     */
    public void onFailure(@NonNull Call<T> call,
                          @NonNull Throwable e) {
        wrapper.onFailureResult();
        if (e instanceof UnknownHostException) {
            mvpView.showMessage("Unknown host exception");
            return;
        }

        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) e).response().errorBody();
            mvpView.showMessage(getErrorMessage(responseBody));

            return;
        }

        if ((e instanceof SocketTimeoutException) || (e instanceof IOException)) {
            mvpView.showMessage("Some exception occurred in server");
            return;
        }

        mvpView.showMessage(null);
    }

    private static String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public interface Wrapper<T> {
        void onSuccessResult(Response<T> response);

        void onFailureResult();
    }
}
