// FragmentSetting.java
package com.example.androidteamproject.Setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidteamproject.ApiData.DataBase;
import com.example.androidteamproject.Login.LoginActivity;
import com.example.androidteamproject.R;
import com.example.androidteamproject.SessionManager;
import com.example.androidteamproject.ThemeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentSetting extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView tv_userid;
    private Button bt_logout, bt_white, bt_dark, bt_update, bt_member_withdrawal;
    String themeColor, userid;

    private Dialog passwordDialog; // 비밀번호 입력 다이얼로그

    // 회원 정보 수정에 필요한 것
    private SessionManager sessionManager;
    private String gender = "", department = "", pwd = null;
    private Spinner spinner_gender, spinner_department;
    private DataBase dataBase; // 데이터베이스 객체 추가

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
            dataBase = new DataBase(); // 데이터베이스 객체 초기화
            userid = sessionManager.getId();

            tv_userid = view.findViewById(R.id.tv_userid);
            tv_userid.setText(userid != null ? userid : "아이디 없음"); // 아이디 설정

            bt_logout = view.findViewById(R.id.bt_logout);
            bt_white = view.findViewById(R.id.bt_white);
            bt_dark = view.findViewById(R.id.bt_dark);
            bt_update = view.findViewById(R.id.bt_update);  // 추가된 부분
            bt_member_withdrawal = view.findViewById(R.id.bt_member_withdrawal);  // 추가된 부분

            // 로그아웃 버튼 클릭시
            bt_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataBase.logout(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("FragmentSetting", "Error logging out", e);
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "로그아웃 실패: 서버와의 통신 오류", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                getActivity().runOnUiThread(() -> {
                                    userid = null;
                                    sessionManager.clearSession();
                                    Toast.makeText(getActivity(), "로그아웃에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    tv_userid.setText(userid);
                                });
                            } else {
                                Log.e("FragmentSetting", "Server returned error: " + response.code());
                                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "로그아웃 실패: " + response.message(), Toast.LENGTH_SHORT).show());
                            }
                        }
                    });
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
                        public void run() {
                            // 비밀번호 확인 후 서버에서 회원 정보 가져오기
                            fetchMemberInfo();
                        }
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
                onSuccess.run();
                passwordDialog.dismiss();
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

    private void fetchMemberInfo() {
        EditText et_input_password = passwordDialog.findViewById(R.id.et_input_password);

        int memberId = sessionManager.getMemberId();
        pwd = String.valueOf(et_input_password.getText());
        Log.d("fetchMemberInfo", String.valueOf(memberId));
        Log.d("fetchMemberInfo", pwd);

        dataBase.getModify(memberId, pwd, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FragmentSetting", "Error fetching member info", e);
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "서버와의 통신 오류", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        int member_id = jsonResponse.getInt("member_id");
                        String name = jsonResponse.getString("name");
                        String gender = jsonResponse.getString("gender");
                        int age = jsonResponse.getInt("age");
                        int departmentId = jsonResponse.getInt("department_id");
                        String email = jsonResponse.getString("email");

                        // SessionManager에 저장
                        sessionManager.settingSession(member_id, name, gender, age, departmentId, email);

                        getActivity().runOnUiThread(() -> showUpdateDialog());

                    } catch (JSONException e) {
                        Log.e("FragmentSetting", "JSON parsing error", e);
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "데이터 처리 오류", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("FragmentSetting", "Server returned error: " + response.code());
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void showUpdateDialog() {
        final Dialog updateDialog = new Dialog(getActivity());
        updateDialog.setContentView(R.layout.dialog_update);

        EditText et_input_name = updateDialog.findViewById(R.id.et_input_name);
        spinner_gender = updateDialog.findViewById(R.id.spinner_gender);
        EditText et_input_age = updateDialog.findViewById(R.id.et_input_age);
        spinner_department = updateDialog.findViewById(R.id.spinner_department);
        EditText et_input_email = updateDialog.findViewById(R.id.et_input_email);
        EditText et_input_pwd = updateDialog.findViewById(R.id.et_input_pwd);
        Button bt_modify_dialog = updateDialog.findViewById(R.id.bt_modify_dialog);

        et_input_name.setText(sessionManager.getName());
        et_input_age.setText(String.valueOf(sessionManager.getAge())); // 수정된 부분
        et_input_email.setText(sessionManager.getEmail());

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.department_array, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_department.setAdapter(departmentAdapter);

        String savedGender = sessionManager.getGender();
        if (savedGender != null) {
            int genderPosition = genderAdapter.getPosition(savedGender);
            spinner_gender.setSelection(genderPosition);
        }

        int savedDepartment = sessionManager.getDepartmentId();
        if (savedDepartment > 0) {
            spinner_department.setSelection(savedDepartment - 1);
        }

        bt_modify_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_input_name.getText().toString();
                String age = et_input_age.getText().toString();
                String email = et_input_email.getText().toString();
                String password = et_input_pwd.getText().toString();
                int department_id = Integer.parseInt(department); // department를 int로 변환

                dataBase.modifyMember(name, gender, age, department_id, userid, password, email, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("FragmentSetting", "Error modifying member info", e);
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "서버와의 통신 오류", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "회원 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                updateDialog.dismiss();
                            });
                        } else {
                            Log.e("FragmentSetting", "Server returned error: " + response.code());
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "회원 정보 수정 실패: " + response.message(), Toast.LENGTH_SHORT).show());
                        }
                    }
                });
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

        spinner_department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department = String.valueOf(id + 1);
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

    // 회원 탈퇴 다이얼로그 보여주기
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
            int heightInDp = 1000;
            float heightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) heightInPx);
        }

        // 삭제 진행
        bt_member_withdrawal.setOnClickListener(view -> {
            if (et_input_text.getText().toString().equals("회원 정보 탈퇴")) {
                // 서버로 회원 탈퇴 요청을 보내는 로직 추가 필요
                memberWithdrawalDialog.dismiss();
                sessionManager.clearSession();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "정확하게 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
