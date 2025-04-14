package webtech.com.courierdriver.Application;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import webtech.com.courierdriver.R;
import webtech.com.courierdriver.activity.ArrivedOrderActivity;
import webtech.com.courierdriver.activity.MainActivity;
import webtech.com.courierdriver.activity.MasterAppCombatActivity;
import webtech.com.courierdriver.firebase.NotificationUtils;

/*
Created by ​
Hannure Abdulrahim

on 9/30/2018.
 */
public class AppController extends Application implements Application.ActivityLifecycleCallbacks {

    private boolean arrivedOrderActivityInForeground;
    private boolean masterAppCombatActivityInForeground;
    private boolean mainActivityInForeground;
    private static final String NEW_ORDER_CHANNEL_ID = "new_order_channel";
    private static final String CANCEL_ORDER_CHANNEL_ID = "cancel_order_channel";
    @Override
    public void onCreate() {
        super.onCreate();
        //register ActivityLifecycleCallbacks

//        FirebaseApp.initializeApp(getApplicationContext());

        registerActivityLifecycleCallbacks(this);

        createNeworderNotificationChannel();
        createCancelNotificationChannel();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {    }

    @Override
    public void onActivityStarted(Activity activity) {    }

    @Override
    public void onActivityResumed(Activity activity) {
        //Here you can add all Activity class you need to check whether its on screen or not

        if(activity instanceof ArrivedOrderActivity)
        {
            arrivedOrderActivityInForeground= true;
        } else if (activity instanceof MasterAppCombatActivity)
        {
            masterAppCombatActivityInForeground = true ;
        } else if (activity instanceof MainActivity)
        {
            mainActivityInForeground = true ;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof ArrivedOrderActivity) {
            arrivedOrderActivityInForeground = false;

        }else if (activity instanceof MasterAppCombatActivity)
        {

            masterAppCombatActivityInForeground = false;
        }else  if (activity instanceof MainActivity)
        {
            mainActivityInForeground=false;
        }

    }



    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    public boolean isMasterAppCombatActivityInForeground() {
        return masterAppCombatActivityInForeground;
    }

    public boolean isArrivedActivityInForeground() {
        return arrivedOrderActivityInForeground;
    }

    public boolean isMainActivityInForeground() {
        return mainActivityInForeground;
    }


//
    private void createNeworderNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = Uri.parse(
                    "android.resource://" +
                            getApplicationContext().getPackageName() +
                            "/" +
                            R.raw.you_have_new_order);
//            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            NotificationChannel channel = new NotificationChannel(
                    NEW_ORDER_CHANNEL_ID,
                    "New Order Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setSound(soundUri, audioAttributes);
//            channel.setSound(null, null);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }



    private void createCancelNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = Uri.parse(
                    "android.resource://" +
                            getApplicationContext().getPackageName() +
                            "/" +
                            R.raw.order_cancelled);
//            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            NotificationChannel channel = new NotificationChannel(
                    CANCEL_ORDER_CHANNEL_ID,
                    "Cancel Order Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setSound(soundUri, audioAttributes);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


}




