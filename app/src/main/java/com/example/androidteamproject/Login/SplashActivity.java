package com.example.androidteamproject.Splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.androidteamproject.Login.LoginActivity;
import com.example.androidteamproject.R;

public class SplashActivity extends Activity {

    private static final int SPLASH_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView iv_ic_book = findViewById(R.id.iv_ic_book);
        final TextView tv_text = findViewById(R.id.tv_text);

        final Animation anime_splash_ball = AnimationUtils.loadAnimation(this, R.anim.anim_splash_ball);

        iv_ic_book.startAnimation(anime_splash_ball);
        tv_text.startAnimation(anime_splash_ball);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
