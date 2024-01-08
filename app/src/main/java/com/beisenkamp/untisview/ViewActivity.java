package com.beisenkamp.untisview;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beisenkamp.untisview.res.SettingsManager;
import com.beisenkamp.untisview.res.UserSettings;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ViewActivity extends AppCompatActivity {

    Timer timer;
    boolean canLeave = false;
    String pw_input = "";

    UserSettings settings;

    @SuppressLint({"SetJavaScriptEnabled", "WakelockTimeout"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // lade Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);

        Tec.updatePasswort(this);

        //Lade Settings
        settings = SettingsManager.getUserSettings(this);

        // verstecke Actionbar, wenn verfügbar
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // verhindern, dass sich das Gerät ausschatet
        // Bildschirm am ausschalten verhindern
//        // CPU am ausschalten verhindern
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
//                "MyApp::MyWakelockTag");
//        wakeLock.acquire();


        // Aktiviere WLAN
        Tec.setWLAN(true, this);

        // bereite Webview vor
        WebView webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // bereite screensafer vor
        ImageView screen_saver_iv = findViewById(R.id.screen_saver_iv);

        // lade lockPlus
        Tec.lockPlus(this);
        setupLockPlus();


        // lade URL
        webView.loadUrl(getString(R.string.server_url) + getString(R.string.server_print_route) + Tec.getSerialNumber() + getString(R.string.param_akku) + Tec.getCharge(this));

        // setze Timerlogik für das Reloaden des Webviews
        timer = new Timer();
        // starte Timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        settings.setMixedContentMode(0);
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    } else {
                        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    Tec.updateRefresh(ViewActivity.this);
                    webView.setSystemUiVisibility(WebView.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    webView.reload();
                });
            }
        }, 0, 1000L * SettingsManager.getUserSettings(ViewActivity.this).getAppRefreshRate()); // refresh_rate (in Sekunden)
    }

    private void setupLockPlus(){
        setupLockPlus(false);
    }

    private void setupLockPlus(boolean unlock) {
        if(SettingsManager.getUserSettings(this).isLockPlus() && !unlock){
            findViewById(R.id.webview).setVisibility(View.INVISIBLE);
            findViewById(R.id.screen_saver_iv).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.webview).setVisibility(View.VISIBLE);
            findViewById(R.id.screen_saver_iv).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_APP_SWITCH || keyCode == KeyEvent.KEYCODE_ALL_APPS || event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            // breche das Event ab
            showCodeInput();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void checkPw(){
        // Check pw
//        canLeave = pw_input.equals(getString(R.string.password));
        settings = SettingsManager.getUserSettings(this);
        canLeave = pw_input.equals(settings.getAppUnlockPassword());
    }

    private void showCodeInput(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // input einrichten
        final EditText input = new EditText(this);
        // Setze Passwort Texttyp
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setTitle(getString(R.string.enter_code_title));
        // Buttons einrichten
        builder.setPositiveButton("OK", (dialog, which) -> {
            pw_input = input.getText().toString();
            checkPw();
            if (canLeave) {
                Toast.makeText(ViewActivity.this, getString(R.string.unlocked_msg), Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(ViewActivity.this, MainActivity.class));
                if(SettingsManager.getUserSettings(this).isLockPlus()) {
                    setupLockPlus(true);
                }
                else {
                    // Schalte Android frei
                    settings.setApp_unlocked(true);
                    SettingsManager.updateUserSettings(settings, this);
                    // beende App
                    finish();
                }
            }
            else {
                AlertDialog.Builder log = new AlertDialog.Builder(ViewActivity.this);

                log.setTitle(getString(R.string.wrong_pw_title));
                // Abschreckung durch "Admin wurde kontaktiert"
                log.setMessage(getString(R.string.wrong_pw_msg));

                log.setPositiveButton("OK", (dialogInterface, i) -> dialog.cancel());
                log.show();
            }
        });
        builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}