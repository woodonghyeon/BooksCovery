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
    Button btJoin;
    Spinner spinner_gender, et_input_department;
    String gender = "", department = "";
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        db = new DataBase();

        EditText et_input_name = findViewById(R.id.et_input_name);
        spinner_gender = findViewById(R.id.spinner_gender);
        EditText et_input_age = findViewById(R.id.et_input_age);
        et_input_department = findViewById(R.id.spinner_department);
        EditText et_input_email = findViewById(R.id.et_input_email);
        EditText et_input_id = findViewById(R.id.et_input_id);
        EditText et_input_pwd = findViewById(R.id.et_input_pwd);

        btJoin = findViewById(R.id.btJoin);

        btJoin.setOnClickListener(view -> {
            String name = et_input_name.getText().toString();
            String age = et_input_age.getText().toString();
            String email = et_input_email.getText().toString();
            String id = et_input_id.getText().toString();
            String pwd = et_input_pwd.getText().toString();

            db.addMember(name, gender, age, department, id, pwd, email, new Callback() {
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
}
