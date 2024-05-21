package com.example.androidteamproject.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidteamproject.Home.HomeActivity;
import com.example.androidteamproject.LoginCheck.LoginCheckActivity;
import com.example.androidteamproject.Join.JoinActivity;
import com.example.androidteamproject.R;

public class LoginActivity extends Activity {

    private EditText et_input_id;
    private EditText et_input_pwd;
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

        //로그인 정보
        et_input_id = findViewById(R.id.et_input_id);
        et_input_pwd = findViewById(R.id.et_input_pwd);

        // 로그인 버튼 클릭시
        bt_login.setOnClickListener(view -> {
            showProgressDialog();
            Intent intent = new Intent(LoginActivity.this, LoginCheckActivity.class);
            intent.putExtra("ID", et_input_id.getText().toString() );
            intent.putExtra("PW", et_input_pwd.getText().toString() );
            startActivityForResult(intent,0);
        });

        // 회원가입 버튼 클릭시
        btJoin.setOnClickListener(view -> { startActivity(new Intent(LoginActivity.this, JoinActivity.class)); });
    }

    private void showProgressDialog(){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("로그인중입니다.");
        dialog.show();
    }

    //로그인 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //결과가 CANCELED = 로그인에 실패함
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),"로그인에 실패했습니다.",Toast.LENGTH_SHORT).show();
        }
        //결과가 OK = 로그인이 되었다.
        else if(resultCode == RESULT_OK){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("Name",data.getStringExtra("Name"));
            intent.putExtra("Gender",data.getStringExtra("Gender"));
            intent.putExtra("Age",data.getIntExtra("Age",0));
            intent.putExtra("Department_id",data.getIntExtra("Department_id",0));
            intent.putExtra("Id",data.getStringExtra("Id"));
            intent.putExtra("Pwd",data.getStringExtra("Pwd"));
            intent.putExtra("Email",data.getStringExtra("Email"));
            intent.putExtra("Mode",data.getStringExtra("Mode"));
            intent.putExtra("UpdateDate",data.getStringExtra("UpdateDate"));
            startActivity(intent);
        }
        
    }
}
