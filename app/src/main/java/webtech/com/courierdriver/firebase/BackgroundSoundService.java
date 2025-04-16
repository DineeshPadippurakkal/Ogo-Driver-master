package webtech.com.courierdriver.firebase;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;


import webtech.com.courierdriver.R;

public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    public static MediaPlayer player;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.order_cancelled);
        player.setLooping(true); // Set looping
        player.setVolume(100, 100);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!player.isPlaying())
            player.start();
        return 1;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onLowMemory() {

    }
}