package com.beisenkamp.untisview;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.beisenkamp.untisview.res.DeviceAdmin;
import com.beisenkamp.untisview.res.SettingsManager;
import com.beisenkamp.untisview.res.UserSettings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;

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
//            try {
//                Toast.makeText(activity, "Nicht autorisierter Zugriff...", Toast.LENGTH_SHORT).show();
//            }catch (Exception ignored){}
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

    public static void updateRefresh(Activity context){

        // Stricedmode needed for JsonAsyncReader
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        UserSettings settings = SettingsManager.getUserSettings(context);

        String url = context.getString(R.string.server_url) + context.getString(R.string.server_password_route);

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new URL(url).openStream());

            JsonObject property = new JsonParser().parse(reader).getAsJsonObject().get("data").getAsJsonObject();
            int refresh_rate = property.get("refreshRate").getAsInt();

            settings.setAppRefreshRate(refresh_rate);
            SettingsManager.updateUserSettings(settings, context);
        } catch (Exception e) {
            e.printStackTrace();
            settings.setAppRefreshRate(context.getResources().getInteger(R.integer.refresh_per_min));
            SettingsManager.updateUserSettings(settings, context);
        }
    }

    public static void updatePasswort(Activity context){

        // Stricedmode needed for JsonAsyncReader
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        UserSettings settings = SettingsManager.getUserSettings(context);

        String url = context.getString(R.string.server_url) + context.getString(R.string.server_password_route);

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new URL(url).openStream());

            JsonObject property = new JsonParser().parse(reader).getAsJsonObject().get("data").getAsJsonObject();
            String pw = property.get("password").getAsString();

            settings.setAppUnlockPassword(pw);
            SettingsManager.updateUserSettings(settings, context);
        } catch (Exception e) {
            e.printStackTrace();
            settings.setAppUnlockPassword(context.getString(R.string.password));
            SettingsManager.updateUserSettings(settings, context);
        }
    }

    public static float getCharge(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float charge = level * 100 / (float)scale;
        Toast.makeText(context, charge + "% Akkuladung", Toast.LENGTH_SHORT).show();

        return charge;
    }

    public static void lockPlus(Activity context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        UserSettings settings = SettingsManager.getUserSettings(context);

        String url = context.getString(R.string.server_url) + context.getString(R.string.server_password_route);

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new URL(url).openStream());

            JsonObject property = new JsonParser().parse(reader).getAsJsonObject().get("data").getAsJsonObject();
            boolean lockPlus = property.get("lockPlus").getAsBoolean();

            settings.setLockPlus(lockPlus);
            SettingsManager.updateUserSettings(settings, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
