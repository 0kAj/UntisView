package com.beisenkamp.untisview;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.beisenkamp.untisview.res.SettingsManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KioskService extends Service {
    private static final long INTERVAL = TimeUnit.SECONDS.toMillis(2);

    private Context ctx = null;
    private boolean running = false;

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    // Kiosk Mode Start
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;
        ctx = this;
        // Check Loop
        Thread t = new Thread(() -> {
            do {
                handleKioskMode();
                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException ignored) {}
            } while (running);
            stopSelf();
        });

        t.start();
        return Service.START_NOT_STICKY;
    }

    private void handleKioskMode() {
        // ist App locked
        if(!isAppUnlocked()) {
            // ist App im Hintergrund
            if(isInBackground()) {
                restoreApp(); // restore!
            }
        }
    }

    private boolean isInBackground() {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return (!ctx.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));
    }

    private void restoreApp() {
        // Restart activity
        Intent i = new Intent(ctx, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }

    public boolean isAppUnlocked() {
        return SettingsManager.getUserSettings(this).isApp_unlocked();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
