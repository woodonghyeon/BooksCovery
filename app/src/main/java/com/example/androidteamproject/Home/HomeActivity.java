package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidteamproject.History.FragmentHistory;
import com.example.androidteamproject.R;
import com.example.androidteamproject.Search.FragmentSearch;
import com.example.androidteamproject.Setting.FragmentSetting;
import com.example.androidteamproject.ThemeUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout ly_home;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ly_home = findViewById(R.id.ly_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 초기 홈 프래그먼트 설정
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.ly_home, new FragmentHome());
            fragmentTransaction.commit();
        }

        SettingListener();
    }

    private void SettingListener() {
        // 선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.tab_home:
                    fragmentTransaction.replace(R.id.ly_home, new FragmentHome());
                    break;
                case R.id.tab_find:
                    fragmentTransaction.replace(R.id.ly_home, new FragmentSearch());
                    break;
                case R.id.tab_history:
                    fragmentTransaction.replace(R.id.ly_home, new FragmentHistory());
                    break;
                case R.id.tab_setting:
                    fragmentTransaction.replace(R.id.ly_home, new FragmentSetting());
                    break;
                default:
                    return false;
            }
            fragmentTransaction.commit();
            return true;
        }
    }
}