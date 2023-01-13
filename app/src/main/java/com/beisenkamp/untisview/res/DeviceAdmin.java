package com.beisenkamp.untisview.res;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        // admin rights
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "admin_receiver_status_disable_warning";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        // admin rights removed
    }
}
