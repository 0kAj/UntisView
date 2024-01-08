package com.beisenkamp.untisview.res;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;

import com.beisenkamp.untisview.R;

public class SettingsManager {

    public static UserSettings getUserSettings(Activity context){
        // verwalte UserSettings
        UserSettings settings = (UserSettings) context.getApplication();
        SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFERENCES,MODE_PRIVATE);
        // Lege Standartwerte für UserSettings fest
        settings.setApp_unlocked(preferences.getBoolean(UserSettings.APP_UNLOCKED_STRING, UserSettings.APP_UNLOCKED_VALUE));
        settings.setAppUnlockPassword(preferences.getString(UserSettings.APP_UNLOCK_PASSWORD, context.getString(R.string.password)));
        settings.setAppRefreshRate(preferences.getInt(UserSettings.APP_REFRESH_RATE_STRING, context.getResources().getInteger(R.integer.refresh_per_min)));
        // gebe UserSettings mit gespeicherten Werten zurück
        return settings;
    }

    public static UserSettings getUserSettings(Service context){
        // verwalte UserSettings
        UserSettings settings = (UserSettings) context.getApplication();
        SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFERENCES,MODE_PRIVATE);
        // Lege Standartwerte für UserSettings fest
        settings.setApp_unlocked(preferences.getBoolean(UserSettings.APP_UNLOCKED_STRING, UserSettings.APP_UNLOCKED_VALUE));
        settings.setAppUnlockPassword(preferences.getString(UserSettings.APP_UNLOCK_PASSWORD, context.getString(R.string.password)));
        settings.setAppRefreshRate(preferences.getInt(UserSettings.APP_REFRESH_RATE_STRING, context.getResources().getInteger(R.integer.refresh_per_min)));
        // gebe UserSettings mit gespeicherten Werten zurück
        return settings;
    }

    public static void updateUserSettings(UserSettings settings, Activity context){
        // verwalte UserSettings
        SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFERENCES,MODE_PRIVATE);
        // bearbeite UserSettings
        SharedPreferences.Editor editor = preferences.edit();
        // ersetze alten Wert durch neuen Wert
        editor.putBoolean(UserSettings.APP_UNLOCKED_STRING, settings.isApp_unlocked());
        editor.putString(UserSettings.APP_UNLOCK_PASSWORD, settings.getAppUnlockPassword());
        editor.putInt(UserSettings.APP_REFRESH_RATE_STRING, settings.getAppRefreshRate());
        // Speichere Änderungen
        editor.apply();
    }
}