package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidteamproject.Search.FragmentSearch;
import com.example.androidteamproject.R;
import com.example.androidteamproject.Setting.FragmentSetting;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout ly_home;
    private BottomNavigationView bottomNavigationView;
    private FragmentHome fragmentHome;
    private FragmentSearch fragmentSearch;
    private FragmentSetting fragmentSetting;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ly_home = findViewById(R.id.ly_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (savedInstanceState == null) {
            // 기본 프래그먼트 설정
            fragmentHome = new FragmentHome();
            fragmentSearch = new FragmentSearch();
            fragmentSetting = new FragmentSetting();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.ly_home, fragmentHome, "home")
                    .commit();
        } else {
            fragmentHome = (FragmentHome) getSupportFragmentManager().findFragmentByTag("home");
            fragmentSearch = (FragmentSearch) getSupportFragmentManager().findFragmentByTag("search");
            fragmentSetting = (FragmentSetting) getSupportFragmentManager().findFragmentByTag("setting");
        }

        SettingListener();

        // onBackPressedDispatcher를 사용하여 뒤로 가기 콜백 등록
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    finish(); // Activity를 종료합니다.
                }
            }
        });
    }

    private void SettingListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment selectedFragment = null;
            String tag = null;

            switch (menuItem.getItemId()) {
                case R.id.tab_home:
                    selectedFragment = fragmentHome;
                    tag = "home";
                    break;
                case R.id.tab_find:
                    selectedFragment = fragmentSearch;
                    tag = "search";
                    break;
                case R.id.tab_history:
                    // 필요시 FragmentHistory 추가
                    break;
                case R.id.tab_setting:
                    selectedFragment = fragmentSetting;
                    tag = "setting";
                    break;
            }

            if (selectedFragment != null && tag != null) {
                replaceFragment(selectedFragment, tag);
                return true;
            }
            return false;
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ly_home, fragment, tag);
        fragmentTransaction.commit();
    }
}
