package com.beisenkamp.untisview.res;

import android.app.Application;

public class UserSettings extends Application {

    public static final String PREFERENCES = "com.beisenkamp.untisview.preferences";
    public static final String APP_UNLOCKED_STRING = "app_unlocked";
    public static final Boolean APP_UNLOCKED_VALUE = true;
    public static final String APP_UNLOCK_PASSWORD = "app_unlock_password";
    public static final String APP_REFRESH_RATE_STRING = "app_refresh_rate";
    public static final String APP_LOCK_PLUS_STRING = "app_lock_plus";
    public static final boolean APP_LOCK_PLUS_VALUE = false;

    private boolean app_unlocked;
    private String appUnlockPassword;
    private int appRefreshRate;
    private boolean lockPlus;

    public boolean isLockPlus() {
        return lockPlus;
    }

    public void setLockPlus(boolean lockPlus) {
        this.lockPlus = lockPlus;
    }

    public int getAppRefreshRate() {
        return appRefreshRate;
    }

    public void setAppRefreshRate(int appRefreshRate) {
        this.appRefreshRate = appRefreshRate;
    }

    public boolean isApp_unlocked() {
        return app_unlocked;
    }

    public void setApp_unlocked(boolean app_unlocked) {
        this.app_unlocked = app_unlocked;
    }

    public String getAppUnlockPassword() {
        return appUnlockPassword;
    }

    public void setAppUnlockPassword(String appUnlockPassword) {
        this.appUnlockPassword = appUnlockPassword;
    }
}
