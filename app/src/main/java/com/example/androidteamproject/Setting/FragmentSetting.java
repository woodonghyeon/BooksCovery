package com.example.androidteamproject.Setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidteamproject.R;
import com.example.androidteamproject.ThemeUtil;

public class FragmentSetting extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView tv_userid;
    private Button bt_logout, bt_white, bt_dark;
    String themeColor;

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
            String userid = sharedPreferences.getString("userid", null);

            tv_userid = view.findViewById(R.id.tv_userid);
            tv_userid.setText(userid);
            bt_logout = view.findViewById(R.id.bt_logout);
            bt_white = view.findViewById(R.id.bt_white);
            bt_dark = view.findViewById(R.id.bt_dark);

            // 로그아웃 버튼 클릭시
            bt_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_userid = null;
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
