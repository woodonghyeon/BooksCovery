package com.example.androidteamproject.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.androidteamproject.Home.HomeActivity;
import com.example.androidteamproject.Join.JoinActivity;
import com.example.androidteamproject.R;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // fadein 애니메이션 설정
        final ImageView iv_ic_book = findViewById(R.id.iv_ic_book);
        final Animation anime_splash_ball = AnimationUtils.loadAnimation(this, R.anim.anim_splash_fadein);
        iv_ic_book.startAnimation(anime_splash_ball);

        Button bt_login;
        bt_login = findViewById(R.id.bt_login);
        Button btJoin = (Button) findViewById(R.id.btJoin);

        // 로그인 버튼 클릭시
        bt_login.setOnClickListener(view -> {
            showProgressDialog();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        });

        // 회원가입 버튼 클릭시
        btJoin.setOnClickListener(view -> {startActivity(new Intent(LoginActivity.this, JoinActivity.class));});
    }

    private void showProgressDialog(){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("로그인중입니다.");
        dialog.show();
    }
}
