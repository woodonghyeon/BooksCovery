package com.example.androidteamproject.Setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidteamproject.Login.LoginActivity;
import com.example.androidteamproject.LoginCheck.LoginCheckActivity;
import com.example.androidteamproject.R;
import com.example.androidteamproject.SessionManager;
import com.example.androidteamproject.ThemeUtil;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FragmentSetting extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView tv_userid;
    private Button bt_logout, bt_white, bt_dark, bt_update, bt_member_withdrawal;
    String themeColor, userid;

    private Dialog passwordDialog; // 비밀번호 입력 다이얼로그

    //회원 정보 수정에 필요한 것
    private SessionManager sessionManager;
    private String gender = "", department = "", pwd = null;
    private Spinner spinner_gender, et_input_department;
    private static Boolean check;

    public FragmentSetting() {
    }

    public static FragmentSetting newInstance(String param1, String param2) {
        FragmentSetting fragment = new FragmentSetting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        Context context = getActivity();
        if (context != null) {
            sessionManager = new SessionManager(context);
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            userid = sharedPreferences.getString("userid", null);

            tv_userid = view.findViewById(R.id.tv_userid);
            tv_userid.setText(userid);
            bt_logout = view.findViewById(R.id.bt_logout);
            bt_white = view.findViewById(R.id.bt_white);
            bt_dark = view.findViewById(R.id.bt_dark);
            bt_update = view.findViewById(R.id.bt_update);  // 추가된 부분
            bt_member_withdrawal = view.findViewById(R.id.bt_member_withdrawal);  // 추가된 부분

            // 로그아웃 버튼 클릭시
            bt_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userid = null;
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    tv_userid.setText(userid);
                    Toast.makeText(getActivity(), "로그아웃에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            // 화이트 테마
            bt_white.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    themeColor = ThemeUtil.LIGHT_MODE;
                    ThemeUtil.applyTheme(themeColor);
                    ThemeUtil.modSave(getContext().getApplicationContext(), themeColor);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ly_home, new FragmentSetting())
                            .commit();
                }
            });
            // 다크 테마
            bt_dark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    themeColor = ThemeUtil.DARK_MODE;
                    ThemeUtil.applyTheme(themeColor);
                    ThemeUtil.modSave(getContext().getApplicationContext(), themeColor);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ly_home, new FragmentSetting())
                            .commit();
                }
            });

            // 수정 버튼 클릭시
            bt_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPasswordDialog(new Runnable() {
                        @Override
                        public void run() { showUpdateDialog(); }
                    });
                }
            });

            // 회원 탈퇴 버튼 클릭시
            bt_member_withdrawal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPasswordDialog(new Runnable() {
                        @Override
                        public void run() {
                            showMemberWithdrawalDialog();
                        }
                    });
                }
            });

        }

        return view;
    }

    private void showPasswordDialog(final Runnable onSuccess) {
        passwordDialog = new Dialog(getActivity());
        passwordDialog.setContentView(R.layout.dialog_input_password);

        EditText et_input_password = passwordDialog.findViewById(R.id.et_input_password);
        Button bt_submit = passwordDialog.findViewById(R.id.bt_submit);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd = String.valueOf(et_input_password.getText());
                new checkPassword(new PasswordCheckCallback() { //비밀번호 검사 메서드
                    @Override
                    public void onPasswordCheckCompleted(boolean isValid) {
                        if (isValid) {
                            passwordDialog.dismiss();
                            onSuccess.run();
                        } else {
                            Toast.makeText(getActivity(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute();
            }
        });

        passwordDialog.show();
        Window window = passwordDialog.getWindow();
        if (window != null) {
            int heightInDp = 500;
            float heightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) heightInPx);
        }
    }

    private interface PasswordCheckCallback {
        void onPasswordCheckCompleted(boolean isValid);
    }

    //비밀번호 검사하기
    private class checkPassword extends AsyncTask<Void, Void, Boolean> {
        private PasswordCheckCallback callback;

        checkPassword(PasswordCheckCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            String DBpwd = null;
            Connection connection = null;

            try {
                connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

                // PreparedStatement 생성
                String query = "SELECT password FROM member_info WHERE id = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, sessionManager.getId());

                // 쿼리 실행 및 결과 가져오기
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    DBpwd = resultSet.getString("password");
                }

                if (pwd != null && DBpwd != null) {
                    // 입력된 비밀번호를 가져온 솔트와 함께 해싱
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    md.update(sessionManager.getPasswordKey().getBytes());
                    md.update(pwd.getBytes());
                    String hashedEnteredPassword = String.format("%064x", new BigInteger(1, md.digest()));

                    return DBpwd.equals(hashedEnteredPassword);
                }

            } catch (Exception e) {
                Log.e("ConnectToDatabaseTask", "Error connecting to database", e);
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
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (callback != null) {
                callback.onPasswordCheckCompleted(aBoolean);
            }
        }
    }

    private void showUpdateDialog() {
        final Dialog updateDialog = new Dialog(getActivity());
        updateDialog.setContentView(R.layout.dialog_update);

        EditText et_input_name = updateDialog.findViewById(R.id.et_input_name);
        spinner_gender = updateDialog.findViewById(R.id.spinner_gender);
        EditText et_input_age = updateDialog.findViewById(R.id.et_input_age);
        et_input_department = updateDialog.findViewById(R.id.spinner_department);
        EditText et_input_email = updateDialog.findViewById(R.id.et_input_email);
        EditText et_input_pwd = updateDialog.findViewById(R.id.et_input_pwd);
        Button bt_modify_dialog = updateDialog.findViewById(R.id.bt_modify_dialog);

        et_input_name.setText(sessionManager.getName());
        et_input_age.setText(String.valueOf(sessionManager.getAge()));
        et_input_email.setText(sessionManager.getEmail());

        bt_modify_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeUpdateSync( et_input_name.getText().toString(), gender, et_input_age.getText().toString(), department, et_input_email.getText().toString(), et_input_pwd.getText().toString());
                updateDialog.dismiss();
            }
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
                department = String.valueOf(id+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        updateDialog.show();
        Window window = updateDialog.getWindow();
        if (window != null) {
            int heightInDp = 1000;
            float heightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) heightInPx);
        }
    }

    //회원 정보 수정 로직
    private void executeUpdateSync(String... params) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<Boolean> futureTask = new FutureTask<>(new UpdateCallable(params));
        executor.execute(futureTask);

        try {
            Boolean result = futureTask.get(); // 동기적으로 결과를 기다림
            UpdateExecute(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }

    private class UpdateCallable implements Callable<Boolean> {
        private String[] params;

        public UpdateCallable(String... params) {
            this.params = params;
        }

        @Override
        public Boolean call() throws Exception {
            String name = params[0];
            String gender = params[1];
            String age = params[2];
            String department = params[3];
            String email = params[4];
            String password = params[5];

            Connection conn = null;
            PreparedStatement pstmt = null;
            String sql = "";

            //비밀번호 암호화
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sessionManager.getPasswordKey().getBytes());
            md.update(password.getBytes());
            password = String.format("%064x", new BigInteger(1, md.digest()));
            Log.v("sadfsadf",password);

            try {
                // JDBC 드라이버 로드
                Class.forName("com.mysql.jdbc.Driver");
                // 데이터베이스에 연결
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

                // 쿼리 실행
                sql = "Update member_info set name = ?, gender = ?, age = ?, department_id = ?, email = ?, password = ? where id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, gender);
                pstmt.setInt(3, Integer.parseInt(age));
                pstmt.setInt(4, Integer.parseInt(department));
                pstmt.setString(5, email);
                pstmt.setString(6, password);
                pstmt.setString(7, sessionManager.getId());
                int result = pstmt.executeUpdate();

                sessionManager.UpdateLoginSession(name, gender, Integer.parseInt(age), Integer.parseInt(department), email);

                conn.close();
                pstmt.close();

                // 결과 받기
                return result > 0;

            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //회원 삭제
    private void showMemberWithdrawalDialog() {
        final Dialog memberWithdrawalDialog = new Dialog(getActivity());
        memberWithdrawalDialog.setContentView(R.layout.dialog_member_withdrawal);

        TextView tv_userid = memberWithdrawalDialog.findViewById(R.id.tv_userid);
        EditText et_input_text = memberWithdrawalDialog.findViewById(R.id.et_input_text);
        Button bt_member_withdrawal = memberWithdrawalDialog.findViewById(R.id.bt_member_withdrawal);
        tv_userid.setText(userid + "님 \n" + "정말 탈퇴하시겠어요?");

        memberWithdrawalDialog.show();
        Window window = memberWithdrawalDialog.getWindow();
        if (window != null) {
            int heightInDp = 700;
            float heightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) heightInPx);
        }

        //삭제 진행
        bt_member_withdrawal.setOnClickListener(view -> {
            if(et_input_text.getText().toString().equals("회원 정보 탈퇴")){
                //회원 탈퇴가 완료되면 다시 처음 페이지로 돌아감 ( 회원 정보가 없으니까 )
                executeDeleteSync();
                memberWithdrawalDialog.dismiss();
                sessionManager.clearSession();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
            else { Toast.makeText(getActivity(),"정확하게 입력해주세요.", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void executeDeleteSync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<Boolean> futureTask = new FutureTask<>(new DeleteCallable());
        executor.execute(futureTask);

        try {
            Boolean result = futureTask.get(); // 동기적으로 결과를 기다림
            DeleteExecute(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }

    private class DeleteCallable implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            Connection conn = null;
            PreparedStatement pstmt = null;
            String sql = "";

            try {
                // JDBC 드라이버 로드
                Class.forName("com.mysql.jdbc.Driver");
                // 데이터베이스에 연결
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

                // 쿼리 실행
                sql = "DELETE FROM member_info WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, sessionManager.getId());
                int result = pstmt.executeUpdate();

                conn.close();
                pstmt.close();

                // 결과 받기
                return result > 0;

            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    //회원 정보 수정, 삭제 결과 출력하기
    private void UpdateExecute(Boolean aBoolean) {
        if (aBoolean) {
            Toast.makeText(super.getContext(), "회원 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(super.getContext(), "회원 정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void DeleteExecute(Boolean aBoolean) {
        if (aBoolean) {
            Toast.makeText(super.getContext(), "회원 정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(super.getContext(), "회원 정보 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
