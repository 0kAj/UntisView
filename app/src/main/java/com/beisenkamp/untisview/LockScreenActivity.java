package com.beisenkamp.untisview;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

public class LockScreenActivity extends ViewActivity {


    @SuppressLint({"SetJavaScriptEnabled", "WakelockTimeout"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // zeige es auf dem LogScreen
        showOnLockScreen();
        // mache alles wie in der View Activity
        super.onCreate(savedInstanceState);
    }

    private void showOnLockScreen(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }
}