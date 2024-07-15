package com.example.androidteamproject.Join;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidteamproject.ApiData.DataBase;
import com.example.androidteamproject.Login.LoginActivity;
import com.example.androidteamproject.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class JoinActivity extends AppCompatActivity {
    Button btJoin, btIdCheck;
    Spinner spinner_gender, et_input_department;
    String gender = "", department = "";
    DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        dataBase = new DataBase();

        EditText et_input_name = findViewById(R.id.et_input_name);
        spinner_gender = findViewById(R.id.spinner_gender);
        EditText et_input_age = findViewById(R.id.et_input_age);
        et_input_department = findViewById(R.id.spinner_department);
        EditText et_input_email = findViewById(R.id.et_input_email);
        EditText et_input_id = findViewById(R.id.et_input_id);
        EditText et_input_pwd = findViewById(R.id.et_input_pwd);
        btJoin = findViewById(R.id.btJoin);
        btIdCheck = findViewById(R.id.bt_idcheck);

        btIdCheck.setOnClickListener(v -> {
            String enteredId = et_input_id.getText().toString().trim();
            if (!enteredId.isEmpty()) {
                checkIdDuplicate(enteredId);
            } else {
                Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });

        btJoin.setOnClickListener(view -> {
            String name = et_input_name.getText().toString();
            String age = et_input_age.getText().toString();
            String email = et_input_email.getText().toString();
            String id = et_input_id.getText().toString();
            String pwd = et_input_pwd.getText().toString();

            addMember(name, gender, age, department, id, pwd, email);
        });

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        et_input_department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department = String.valueOf(id + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void checkIdDuplicate(String id) {
        dataBase.checkIdDuplicate(id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("JoinActivity", "Error checking ID", e);
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "아이디 중복 체크 실패: 서버와의 통신 오류", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    try {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            if (responseBody.contains("회원가입 가능한 아이디입니다.")) {
                                Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "이미 사용 중인 아이디입니다", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "아이디 중복 체크 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e("JoinActivity", "Error processing response", e);
                        Toast.makeText(getApplicationContext(), "아이디 중복 체크 실패: 응답 처리 오류", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addMember(String name, String gender, String age, String department_id, String id, String password, String email) {
        dataBase.addMember(name, gender, age, department_id, id, password, email, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("JoinActivity", "Error sending data to server", e);
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "회원가입 실패: 서버와의 통신 오류", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("JoinActivity", "Data sent successfully");
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(JoinActivity.this, LoginActivity.class));
                    });
                } else {
                    Log.e("JoinActivity", "Server returned error: " + response.code());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "회원가입 실패: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
