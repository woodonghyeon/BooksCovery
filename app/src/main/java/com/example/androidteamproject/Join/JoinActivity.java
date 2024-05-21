package com.example.androidteamproject.Join;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

import com.example.androidteamproject.Login.LoginActivity;
import com.example.androidteamproject.R;

public class JoinActivity extends AppCompatActivity {
    Button btJoin;
    Spinner spinner_gender, et_input_department;
    String gender, department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        EditText et_input_name = findViewById(R.id.et_input_name);
        spinner_gender = (Spinner) findViewById(R.id.spinner_gender);
        EditText et_input_age = findViewById(R.id.et_input_age);
        et_input_department = findViewById(R.id.spinner_department);
        EditText et_input_email = findViewById(R.id.et_input_email);
        EditText et_input_id = findViewById(R.id.et_input_id);
        EditText et_input_pwd = findViewById(R.id.et_input_pwd);

        btJoin = findViewById(R.id.btJoin);

        // 데이터베이스 연결 시작
        btJoin.setOnClickListener(view -> {
            String name = et_input_name.getText().toString();
            gender = "";
            String age = et_input_age.getText().toString();
            department = "";
            String email = et_input_email.getText().toString();
            String id = et_input_id.getText().toString();
            String pwd = et_input_pwd.getText().toString();

            new ConnectToDatabaseTask().execute(name, gender, age, department, email, id, pwd);
        });

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String) parent.getItemAtPosition(position);
                Log.v("123",gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        et_input_department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department = String.valueOf(id+1);
                Log.v("123",department);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private class ConnectToDatabaseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String name = params[0];
            String gender = params[1];
            String age = params[2];
            String department = params[3];
            String email = params[4];
            String id = params[5];
            String pwd = params[6];

            // 현재 시간 가져오기
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = dateFormat.format(calendar.getTime());

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                // JDBC 드라이버 로드
                Class.forName("com.mysql.jdbc.Driver");
                // 데이터베이스에 연결 (url : "jdbc:mysql://10.0.2.2 (에뮬레이터 로컬 호스트 주소) :3306/your-database-name", user : DB 아이디, password : DB 비밀번호)
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");
                
                //비밀번호 암호화
                SecureRandom random = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    random = SecureRandom.getInstanceStrong();
                }
                byte[] bytes = new byte[16];
                random.nextBytes(bytes);
                String salt = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    salt = new String(Base64.getEncoder().encode(bytes));
                }

                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt.getBytes());
                md.update(pwd.getBytes());
                String hex = String.format("%064x", new BigInteger(1, md.digest()));

                // 쿼리 실행
                String sql = "Insert into member_info (name, gender, age, department_id, email, id, password, password_key, mode, update_date) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, gender);
                pstmt.setInt(3, Integer.parseInt(age));
                pstmt.setInt(4, Integer.parseInt(department));
                pstmt.setString(5, email);
                pstmt.setString(6, id);
                pstmt.setString(7, hex);
                pstmt.setString(8, salt);
                pstmt.setString(9, "w");
                pstmt.setString(10, currentTime);

                // 쿼리 실행
                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0 ? "데이터 삽입 성공" : "데이터 삽입 실패";

            } catch (Exception e) {
                Log.e("InsertDataTask", "Error inserting data", e);
                return "데이터 삽입 중 오류 발생";

            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (Exception e) {
                    Log.e("InsertDataTask", "Error closing connection", e);
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            startActivity(new Intent(JoinActivity.this, LoginActivity.class));
            // 결과를 Toast로 표시
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }
}

