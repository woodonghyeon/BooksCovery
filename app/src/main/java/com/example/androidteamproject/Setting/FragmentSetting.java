package com.example.androidteamproject.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        }

        return view;
    }
}
