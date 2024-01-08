package com.beisenkamp.untisview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beisenkamp.untisview.res.CustomViewGroup;
import com.beisenkamp.untisview.res.SettingsManager;
import com.beisenkamp.untisview.res.UserSettings;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver screenOnOBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Tec.setUpKeybord(this) && Tec.isDeviceAdmin(this)) {
            screenOnOBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

                    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                        // screen off
                        context.startActivity(new Intent(context, LockScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    if (intent.getAction().equals(Intent.ACTION_USER_PRESENT) && !keyguardManager.inKeyguardRestrictedInputMode()) {
                        // screen on
                    } else {
                        // screen off
                        context.startActivity(new Intent(context, LockScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            };

            //region disable dropdown
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Please give UntisView this permission!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1);
                } else {
                    preventStatusBarExpansion(MainActivity.this);
                }
            } else {
                preventStatusBarExpansion(MainActivity.this);
            }
            //endregion

            // neuladen des Receivers
            try {
                unregisterScreenLockStateBrodcastReceiver(this);
            } catch (Exception ignored) {
            }
            registerScreenLockStateBrodcastReceiver(this);

            // lade Buttons
            Button setting = findViewById(R.id.settings_btn);
            setting.setVisibility(View.VISIBLE);
            setting.setOnClickListener(view -> {
                // öffne Einstellungen
                UserSettings settings = SettingsManager.getUserSettings(this);
                if (settings.isApp_unlocked()) {
                    Intent settings_intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(settings_intent);
                } else {
                    Tec.lauchEvent(this);
                }
            });
            Button other_app = findViewById(R.id.other_app_btn);
            other_app.setVisibility(View.VISIBLE);
            other_app.setOnClickListener(view -> {
                // Starte App Auswahlmenü
                UserSettings settings = SettingsManager.getUserSettings(this);
                if (settings.isApp_unlocked()) {
                    Tec.lauchLauncher(this);
                } else {
                    Tec.lauchEvent(this);
                }
            });
            Button close = findViewById(R.id.close_btn);
            close.setVisibility(View.VISIBLE);
            close.setOnClickListener(view -> {
                // sperre Gerät
                UserSettings settings = SettingsManager.getUserSettings(this);
                settings.setApp_unlocked(false);
                SettingsManager.updateUserSettings(settings, this);

                DevicePolicyManager deviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                deviceManger.lockNow();
            });

            startService(new Intent(this, KioskService.class));
        }
        else {
            findViewById(R.id.close_btn).setVisibility(View.INVISIBLE);
            findViewById(R.id.other_app_btn).setVisibility(View.INVISIBLE);
            findViewById(R.id.settings_btn).setVisibility(View.INVISIBLE);
            findViewById(R.id.state_tv).setOnClickListener((view -> {
                if(Tec.setUpKeybord(this)){
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tec.lauchEvent(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Tec.lauchEvent(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tec.lauchEvent(this);
    }

    public void registerScreenLockStateBrodcastReceiver(Context context){
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        context.getApplicationContext().registerReceiver(screenOnOBroadcastReceiver, filter);
    }

    public void unregisterScreenLockStateBrodcastReceiver(Context context){
        context.unregisterReceiver(screenOnOBroadcastReceiver);
    }


    public static void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        Activity activity = (Activity)context;
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        @SuppressLint("InternalInsetResource") int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }

        localLayoutParams.height = result;

        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(context);

        manager.addView(view, localLayoutParams);
    }

}