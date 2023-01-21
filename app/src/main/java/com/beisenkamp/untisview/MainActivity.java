package com.beisenkamp.untisview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beisenkamp.untisview.res.DeviceAdmin;
import com.beisenkamp.untisview.res.SettingsManager;
import com.beisenkamp.untisview.res.UserSettings;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver screenOnOBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenOnOBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    // screen off
                    context.startActivity(new Intent(context, LockScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                if(intent.getAction().equals(Intent.ACTION_USER_PRESENT) && !keyguardManager.inKeyguardRestrictedInputMode()){
                    // screen on
                }
                else {
                    // screen off
                    context.startActivity(new Intent(context,LockScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        };

        // neuladen des Receivers
        try {
            unregisterScreenLockStateBrodcastReceiver(this);
        }catch (Exception ignored){}
        registerScreenLockStateBrodcastReceiver(this);

        // lade Buttons
        Button setting = findViewById(R.id.settings_btn);
        setting.setOnClickListener(view -> {
            // öffne Einstellungen
            Intent settings_intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(settings_intent);
        });
        Button other_app = findViewById(R.id.other_app_btn);
        other_app.setOnClickListener(view -> {
            // Starte App Auswahlmenü
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        });
        Button close = findViewById(R.id.close_btn);
        close.setOnClickListener(view -> {
            DevicePolicyManager deviceManger = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

            boolean active = deviceManger.isAdminActive(new ComponentName(this, DeviceAdmin.class));
            if (active) {
                // sperre Gerät
                UserSettings settings = SettingsManager.getUserSettings(this);
                settings.setApp_unlocked(false);
                SettingsManager.updateUserSettings(settings,this);

                deviceManger.lockNow();
            }
            else {
                Toast.makeText(this, "Aktiviere " + this.getString(R.string.app_name) + " als Geräteadministrator", Toast.LENGTH_LONG).show();
                startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lade UserSettings
        UserSettings settings = SettingsManager.getUserSettings(this);

        if(!settings.isApp_unlocked()){
            // Simuliere klick für close_btn
            findViewById(R.id.close_btn).callOnClick();
            Toast.makeText(this, "Nicht autorisierter Zugriff...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onResume();
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
}