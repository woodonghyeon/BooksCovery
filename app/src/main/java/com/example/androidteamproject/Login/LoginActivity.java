package com.example.androidteamproject.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidteamproject.ApiData.SearchBookKeyword;
import com.example.androidteamproject.Home.HomeActivity;
import com.example.androidteamproject.LoginCheck.LoginCheckActivity;
import com.example.androidteamproject.Join.JoinActivity;
import com.example.androidteamproject.R;
import com.example.androidteamproject.SessionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {
    ProgressDialog dialog;
    private EditText et_input_id;
    private EditText et_input_pwd;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

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
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("로그인중입니다.");
        dialog.show();
    }

    //로그인 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_OK) {
            String userid = data.getStringExtra("Id");
            int member_id ;
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            List<SearchBookKeyword> books = new ArrayList<>();
            try {
                // JDBC 드라이버 로드
                Class.forName("com.mysql.jdbc.Driver");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                // 데이터베이스에 연결 (url : "jdbc:mysql://10.0.2.2 (에뮬레이터 로컬 호스트 주소) :3306/your-database-name", user : DB 아이디, password : DB 비밀번호)
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

                // 쿼리 실행
                String sql = "select member_id from member_info where id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, userid);

                // 쿼리 실행
                rs = pstmt.executeQuery();
                if(rs.next()) {
                    member_id = rs.getInt("member_id");
                }
            } catch (Exception e) {
                Log.e("InsertDataTask", "Error inserting data", e);
//            return "데이터 삽입 중 오류 발생";

            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (Exception e) {
                    Log.e("InsertDataTask", "Error closing connection", e);
                }
            }

            Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
            // SharedPreferences에 userid 저장
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userid", userid);
            editor.apply();

            //세션 매니저 만들었는데 위에꺼 필요할까?
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.createLoginSession(
                    data.getIntExtra("Member",1),
                    data.getStringExtra("Name"),
                    data.getStringExtra("Gender"),
                    data.getIntExtra("Age", 0),
                    data.getIntExtra("Department_id", 0),
                    data.getStringExtra("Id"),
                    data.getStringExtra("Password_Key"),
                    data.getStringExtra("Email"),
                    data.getStringExtra("Mode"),
                    data.getStringExtra("UpdateDate")
            );
            // HomeActivity로 이동
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
