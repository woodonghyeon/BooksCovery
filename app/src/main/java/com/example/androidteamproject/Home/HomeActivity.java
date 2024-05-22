package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidteamproject.R;
import com.example.androidteamproject.Search.FragmentSearch;
import com.example.androidteamproject.Setting.FragmentSetting;
import com.example.androidteamproject.ThemeUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class HomeActivity extends AppCompatActivity {
    private LinearLayout ly_home;
    private BottomNavigationView bottomNavigationView;
    String themeColor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        themeColor = ThemeUtil.modLoad(getApplicationContext());
        ThemeUtil.applyTheme(themeColor);

        ly_home = findViewById(R.id.ly_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // 초기 홈 프래그먼트 설정
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ly_home, new FragmentHome());
        fragmentTransaction.commit();
        SettingListener();
    }
    private void SettingListener() {
        // 선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }
    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.tab_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ly_home, new FragmentHome())
                        .commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.tab_find) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ly_home, new FragmentSearch())
                        .commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.tab_history) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ly_home, new FragmentHome())
                        .commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.tab_setting) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ly_home, new FragmentSetting())
                        .commit();
                return true;
            }
            return false;
        }
    }
}