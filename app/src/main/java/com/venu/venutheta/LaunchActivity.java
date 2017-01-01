package com.venu.venutheta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;
import com.venu.venutheta.auth.LoginActivity;
import com.venu.venutheta.services.GeneralService;

public class LaunchActivity extends AppCompatActivity {
    public static final int SPLASH_SCREEN_TIMEOUT = 5000;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        handler = new Handler();
        scheduleRedirect();


    }
    private void scheduleRedirect() {
        new Handler().postDelayed(() -> {
            if (ParseUser.getCurrentUser() != null) {
                GeneralService.startActionStartUp(LaunchActivity.this.getApplicationContext());
                startActivity(new Intent(LaunchActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }else
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }, SPLASH_SCREEN_TIMEOUT);
    }
}
