package com.beisenkamp.untisview;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.beisenkamp.untisview.res.DeviceAdmin;
import com.beisenkamp.untisview.res.SettingsManager;
import com.beisenkamp.untisview.res.UserSettings;

public class Tec {

    @SuppressLint("HardwareIds")
    public static String getSerialNumber(){
        // Rückgabe von der Seriennummner
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // bei neueren Geräten ab Android 8
            return Build.getSerial();
        }
        // bei alten Geräten bis Android 7.1
        return Build.SERIAL;
    }

    public static void setWLAN(boolean status, Context context) {
        // greife auf WLAN-Manager zu
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // setzte WLAN auf status
        wifiManager.setWifiEnabled(status);
    }

    public static void lauchEvent(Activity activity){
        // Lade UserSettings
        UserSettings settings = SettingsManager.getUserSettings(activity);

        if(!settings.isApp_unlocked()){
            // Simuliere klick für close_btn
            activity.findViewById(R.id.close_btn).callOnClick();
            try {
                Toast.makeText(activity, "Nicht autorisierter Zugriff...", Toast.LENGTH_SHORT).show();
            }catch (Exception ignored){}
        }
    }

    public static void lauchLauncher(Context context){
        context.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER));
    }

    public static boolean setUpKeybord(Activity context){
        Log.d("LOG",Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD));
        if(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS).contains("com.beisenkamp.untisview/.UntisViewKeyboard")){
            ((TextView) context.findViewById(R.id.state_tv)).setText(R.string.state_setdefaultinput);
            if(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD).contains("com.beisenkamp.untisview/.UntisViewKeyboard")){
                return true;
            }
            Toast.makeText(context, "Lege das Keyboard als Standard fest!", Toast.LENGTH_SHORT).show();
            InputMethodManager imeManager = (InputMethodManager) context.getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imeManager.showInputMethodPicker();
            return false;
        }
        ((TextView) context.findViewById(R.id.state_tv)).setText(R.string.state_setinputenabled);
        Toast.makeText(context, "Aktiviere UntisView!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        context.startActivity(i);
        return false;
    }

    public static boolean isDeviceAdmin(Activity activity){
        DevicePolicyManager deviceManger = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);

        boolean active = deviceManger.isAdminActive(new ComponentName(activity, DeviceAdmin.class));
        if(!active){
            ((TextView) activity.findViewById(R.id.state_tv)).setText(R.string.state_needdeviceadmin);
            Toast.makeText(activity, "Aktiviere " + activity.getString(R.string.app_name) + " als Geräteadministrator", Toast.LENGTH_LONG).show();
            activity.startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
            return false;
        }
        ((TextView) activity.findViewById(R.id.state_tv)).setText(R.string.state_active);
        return true;
    }
}
