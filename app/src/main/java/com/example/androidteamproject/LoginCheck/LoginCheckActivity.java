package com.example.androidteamproject.LoginCheck;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidteamproject.Join.JoinActivity;
import com.example.androidteamproject.Login.LoginActivity;
import com.example.androidteamproject.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginCheckActivity extends AppCompatActivity {

    //setId, setPw -> 사용자가 입력한 id, pw
    String setId, setPw;
    String name,gender,id,pwd,password_key,email,mode;
    int age,department_id;
    //String updateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent inIntent = getIntent();
        setId = inIntent.getStringExtra("ID");
        setPw = inIntent.getStringExtra("PW");

        new ConnectToDatabaseTask().execute();

    }

    private class ConnectToDatabaseTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                // JDBC 드라이버 로드
                Class.forName("com.mysql.jdbc.Driver");
                // 데이터베이스에 연결 (url : "jdbc:mysql://10.0.2.2 (에뮬레이터 로컬 호스트 주소) :3306/your-database-name", user : DB 아이디, password : DB 비밀번호)
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

                // 쿼리 실행
                String sql = "Select * from member_info where id = ? && password = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, setId);

                //비밀번호 암호화 체크
                String[] passwordAndSalt = getPasswordAndSaltFromDatabase(setId);
                assert passwordAndSalt != null;
                String pwd = passwordAndSalt[0];
                String salt = passwordAndSalt[1];

                if(pwd != null && setPw != null){
                    // 입력된 비밀번호를 가져온 솔트와 함께 해싱
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    md.update(salt.getBytes());
                    md.update(setPw.getBytes());
                    String hashedEnteredPassword = String.format("%064x", new BigInteger(1, md.digest()));

                    if(pwd.equals(hashedEnteredPassword)){
                        pstmt.setString(2, hashedEnteredPassword);
                        rs = pstmt.executeQuery();
                    }
                    else {
                        Log.e("ConnectToDatabaseTask", "Error connecting to database");
                        return null;
                    }
                }

                //결과 받기
                while (rs.next()) {
                    name = rs.getString("name");
                    gender = rs.getString("gender");
                    age = rs.getInt("age");
                    department_id = rs.getInt("department_id");
                    id = rs.getString("id");
                    pwd = rs.getString("password");
                    password_key = rs.getString("password_key");
                    email = rs.getString("email");
                    mode = rs.getString("mode");
                    //updateDate = rs.getString("update_date");
                }

                //끝
                rs.close();
                pstmt.close();
                conn.close();
                return null;
            } catch (Exception e) {
                Log.e("ConnectToDatabaseTask", "Error connecting to database", e);
                return null;
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (Exception e) {
                    Log.e("ConnectToDatabaseTask", "Error closing connection", e);
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //결과값을 구분해서 있으면 OK , 없으면 CANCELED
            Intent outIntent = new Intent(LoginCheckActivity.this, LoginActivity.class);

            if(name==null){
                setResult(RESULT_CANCELED);
                finish();
            }
            else{
                outIntent.putExtra("Name",name);
                outIntent.putExtra("Gender",gender);
                outIntent.putExtra("Age",age);
                outIntent.putExtra("Department_id",department_id);
                outIntent.putExtra("Id",id);
                outIntent.putExtra("Pwd",pwd);
                outIntent.putExtra("Password_Key",password_key);
                outIntent.putExtra("Email",email);
                outIntent.putExtra("Mode",mode);
                //outIntent.putExtra("UpdateDate",updateDate);
                setResult(RESULT_OK,outIntent);
                finish();
            }
        }

        private String[] getPasswordAndSaltFromDatabase(String userId) throws SQLException {
            String[] result = new String[2];

            // 데이터베이스 연결
            Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                // PreparedStatement 생성
                String query = "SELECT password, password_key FROM member_info WHERE id = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, userId);

                // 쿼리 실행 및 결과 가져오기
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    result[0] = resultSet.getString("password");
                    result[1] = resultSet.getString("password_key");
                }
            } catch (Exception e) {
                Log.e("ConnectToDatabaseTask", "Error connecting to database", e);
                return null;
                // 오류 처리
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                    if (resultSet != null) resultSet.close();
                } catch (Exception e) {
                    Log.e("ConnectToDatabaseTask", "Error closing connection", e);
                }
            }
            return result;
        }


    }

}
