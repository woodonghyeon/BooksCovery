// LoginActivity.java
package com.example.androidteamproject.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidteamproject.ApiData.DataBase;
import com.example.androidteamproject.Home.HomeActivity;
import com.example.androidteamproject.Join.JoinActivity;
import com.example.androidteamproject.R;
import com.example.androidteamproject.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends Activity {
    ProgressDialog dialog;
    private EditText et_input_id;
    private EditText et_input_pwd;
    DataBase dataBase = new DataBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView iv_ic_book = findViewById(R.id.iv_ic_book);
        final Animation anime_splash_ball = AnimationUtils.loadAnimation(this, R.anim.anim_splash_fadein);
        iv_ic_book.startAnimation(anime_splash_ball);

        Button bt_login = findViewById(R.id.bt_login);
        Button btJoin = findViewById(R.id.btJoin);

        et_input_id = findViewById(R.id.et_input_id);
        et_input_pwd = findViewById(R.id.et_input_pwd);

        bt_login.setOnClickListener(view -> {
            showProgressDialog();
            String id = et_input_id.getText().toString();
            String password = et_input_pwd.getText().toString();
            dataBase.login(id, password, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "로그인 실패: 서버와의 통신 오류", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    });
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "로그인 실패: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        });

        btJoin.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, JoinActivity.class)));
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("로그인중입니다.");
        dialog.show();
    }
}
