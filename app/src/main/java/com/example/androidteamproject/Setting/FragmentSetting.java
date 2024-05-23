package com.example.androidteamproject.Setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidteamproject.Login.LoginActivity;
import com.example.androidteamproject.R;
import com.example.androidteamproject.ThemeUtil;

public class FragmentSetting extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView tv_userid;
    private Button bt_logout, bt_white, bt_dark, bt_update, bt_member_withdrawal;
    String themeColor, userid;

    private Dialog passwordDialog; // 비밀번호 입력 다이얼로그

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
                        public void run() {
                            showUpdateDialog();
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

        Button bt_submit = passwordDialog.findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordDialog.dismiss();
                onSuccess.run();
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

    private void showUpdateDialog() {
        final Dialog updateDialog = new Dialog(getActivity());
        updateDialog.setContentView(R.layout.dialog_update);

        EditText et_input_name = updateDialog.findViewById(R.id.et_input_name);
        Spinner spinner_gender = updateDialog.findViewById(R.id.spinner_gender);
        EditText et_input_age = updateDialog.findViewById(R.id.et_input_age);
        Spinner et_input_department = updateDialog.findViewById(R.id.spinner_department);
        EditText et_input_email = updateDialog.findViewById(R.id.et_input_email);
        EditText et_input_pwd = updateDialog.findViewById(R.id.et_input_pwd);
        Button bt_modify_dialog = updateDialog.findViewById(R.id.bt_modify_dialog);

        bt_modify_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
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

    private void showMemberWithdrawalDialog() {
        final Dialog memberWithdrawalDialog = new Dialog(getActivity());
        memberWithdrawalDialog.setContentView(R.layout.dialog_member_withdrawal);

        TextView tv_userid = memberWithdrawalDialog.findViewById(R.id.tv_userid);
        tv_userid.setText(userid + "님 \n" + "정말 탈퇴하시겠어요?");

        memberWithdrawalDialog.show();
        Window window = memberWithdrawalDialog.getWindow();
        if (window != null) {
            int heightInDp = 1000;
            float heightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) heightInPx);
        }
    }
}
