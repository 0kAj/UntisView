package com.beisenkamp.untisview.res;

import android.app.Application;

public class UserSettings extends Application {

    public static final String PREFERENCES = "com.beisenkamp.untisview.preferences";
    public static final String APP_UNLOCKED_STRING = "app_unlocked";
    public static final Boolean APP_UNLOCKED_VALUE = true;

    private boolean app_unlocked;

    public boolean isApp_unlocked() {
        return app_unlocked;
    }

    public void setApp_unlocked(boolean app_unlocked) {
        this.app_unlocked = app_unlocked;
    }
}
