package com.brilltech.jelp.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationCompat;

import com.brilltech.jelp.R;
import com.brilltech.jelp.activities.dashboard.DashBoardActivity;
import com.brilltech.jelp.utils.AppUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    static NotificationManager notificationManager;
    String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        String jsonString = remoteMessage.getData().get("data");
        AppUtils.showLog(TAG, "remote message: " + jsonString);

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent activityIntent = new Intent(this, DashBoardActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent,
                    0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.WHITE);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            AppUtils.showLog(TAG, "details: " + jsonObject.get("alertTitle"));
            AppUtils.showLog(TAG, "details: " + jsonObject.get("alertBody"));
//            AppUtils.showLog(TAG, "details: " + jsonObject.get("category"));

            String category = jsonObject.getString("category");
            String alertTitle = jsonObject.getString("alertTitle");

            double currentLat = DashBoardActivity.lat;
            double currentLng = DashBoardActivity.lng;

            double lat = Double.valueOf(jsonObject.getString("latitude"));
            double lng = Double.valueOf(jsonObject.getString("longitude"));

            double distance = meterDistanceBetweenPoints((float) currentLat, (float) currentLng,
                    (float) lat, (float) lng);

            AppUtils.showLog(TAG, "distance: " + distance);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(jsonObject.getString("alertTitle"))
                    .setContentText(jsonObject.getString("alertBody"))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(jsonObject.getString("alertBody")))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);

            if (distance >= 2000) notificationManager.notify(0, notificationBuilder.build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class NotificationListener extends NotificationListenerService {

        @Override
        public void onNotificationPosted(StatusBarNotification sbn) {
            super.onNotificationPosted(sbn);

            AppUtils.showLog(TAG, "Notification details: " + sbn.getKey());
            AppUtils.showLog(TAG, "Notification details: " + sbn.getNotification().tickerText);

//            RegisterSuccess.mRegisterNow.setVisibility(View.GONE);

        }
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

}