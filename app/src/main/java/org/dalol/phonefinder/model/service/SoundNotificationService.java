package org.dalol.phonefinder.model.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.dalol.phonefinder.R;
import org.dalol.phonefinder.view.activity.MainActivity;

/**
 * Created by Filippo-TheAppExpert on 8/10/2015.
 */
public class SoundNotificationService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = SoundNotificationService.class.getSimpleName();
    private MediaPlayer mPlayer;
    private boolean mRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        mRunning = false;
        mPlayer = MediaPlayer.create(this, R.raw.sound);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnPreparedListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //Toast.makeText(getApplicationContext(), "onCompletion", Toast.LENGTH_SHORT).show();
        cancelNotification();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
        cancelNotification();
        stopSelf();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //Toast.makeText(getApplicationContext(), "onPrepared", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Service is starting onStartCommand");

        if (!mRunning) {
            mRunning = true;
            createNotification();
            mPlayer.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onStop() {
        mPlayer.stop();
        mPlayer.release();
    }

    public void onPause() {
        mPlayer.stop();
        mPlayer.release();
    }

    @Override
    public void onDestroy() {
        mPlayer.stop();
        mPlayer.release();
        cancelNotification();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        cancelNotification();
        stopSelf();
    }

    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("key", "Filippo");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


        // Build notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("ፉጨት Phone Finder")
                .setContentTitle("My notification")
                .setAutoCancel(true)
                .setContentText("Notification Text Message!")
                .setContentIntent(pIntent); //Required on Gingerbread and below

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
        getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        getSystemService(NOTIFICATION_SERVICE);
    }
}
