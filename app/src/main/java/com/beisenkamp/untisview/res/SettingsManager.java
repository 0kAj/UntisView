package com.beisenkamp.untisview.res;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;

public class SettingsManager {

    public static UserSettings getUserSettings(Activity context){
        // verwalte UserSettings
        UserSettings settings = (UserSettings) context.getApplication();
        SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFERENCES,MODE_PRIVATE);
        // Lege Standartwerte für UserSettings fest
        settings.setApp_unlocked(preferences.getBoolean(UserSettings.APP_UNLOCKED_STRING, UserSettings.APP_UNLOCKED_VALUE));
        // gebe UserSettings mit gespeicherten Werten zurück
        return settings;
    }

    public static UserSettings getUserSettings(Service context){
        // verwalte UserSettings
        UserSettings settings = (UserSettings) context.getApplication();
        SharedPreferences preferences = context.getSharedPreferences(UserSettings.PREFERENCES,MODE_PRIVATE);
        // Lege Standartwerte für UserSettings fest
        settings.setApp_unlocked(preferences.getBoolean(UserSettings.APP_UNLOCKED_STRING, UserSettings.APP_UNLOCKED_VALUE));
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
        // Speichere Änderungen
        editor.apply();
    }
}