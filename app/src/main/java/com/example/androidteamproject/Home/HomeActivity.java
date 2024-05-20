package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidteamproject.Search.FragmentSearch;
import com.example.androidteamproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout ly_home;
    private BottomNavigationView bottomNavigationView;
    private FragmentHome fragmentHome;
    private FragmentSearch fragmentSearch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment selectedFragment = null;

            switch (menuItem.getItemId()) {
                case R.id.tab_home:
                    if (fragmentHome == null) {
                        fragmentHome = new FragmentHome();
                        fragmentTransaction.add(R.id.ly_home, fragmentHome, "home");
                    }
                    selectedFragment = fragmentHome;
                    break;
                case R.id.tab_find:
                    if (fragmentSearch == null) {
                        fragmentSearch = new FragmentSearch();
                        fragmentTransaction.add(R.id.ly_home, fragmentSearch, "search");
                    }
                    selectedFragment = fragmentSearch;
                    break;
                case R.id.tab_history:
                    // 필요시 FragmentHistory 추가
                    break;
                case R.id.tab_setting:
                    // 필요시 FragmentSetting 추가
                    break;
            }
            if (selectedFragment != null) {
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment == selectedFragment) {
                        fragmentTransaction.show(fragment);
                    } else {
                        fragmentTransaction.hide(fragment);
                    }
                }
                fragmentTransaction.commit();
                return true;
            }
            return false;
        }
    }
}
