package com.beisenkamp.untisview;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ViewActivity extends AppCompatActivity {

    Timer timer;
    boolean canLeave = false;
    String pw_input = "";

    @SuppressLint({"SetJavaScriptEnabled", "WakelockTimeout"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // lade Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);

        // verstecke Actionbar, wenn verfügbar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // verhindern, dass sich das Gerät ausschatet
        // Bildschirm am ausschalten verhindern
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // CPU am ausschalten verhindern
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();


        // Aktiviere WLAN
        Tec.setWLAN(true, this);

        // bereite Webview vor
        WebView webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // lade URL
        webView.loadUrl(getString(R.string.server_url) + Tec.getSerialNumber());

        // setze Timerlogik für das Reloaden des Webviews
        timer = new Timer();
        // starte Timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() ->{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        settings.setMixedContentMode(0);
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    } else {
                        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    webView.setSystemUiVisibility(WebView.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    webView.reload();
                });
            }
        }, 0, 60000L / getResources().getInteger(R.integer.refresh_per_min)); // 1 min / refresh_per_min
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
        canLeave = pw_input.equals(getString(R.string.password));
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
                startActivity(new Intent(ViewActivity.this, MainActivity.class));
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
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
