package com.beisenkamp.untisview.res;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.beisenkamp.untisview.R;

public class DeviceAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        // admin rights
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.app_name) + " funktioniert dann nicht mehr richtig";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        // admin rights removed
    }
}
