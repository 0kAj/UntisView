package com.beisenkamp.untisview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;

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
}
