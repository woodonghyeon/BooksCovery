package com.example.androidteamproject;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import me.relex.circleindicator.CircleIndicator3;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout ly_home;
    private BottomNavigationView bottomNavigationView;

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
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ly_home, new FragmentHome())
                            .commit();
                    return true;
                }
            }
            switch (menuItem.getItemId()) {
                case R.id.tab_2: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ly_home, new FragmentHome())
                            .commit();
                    return true;
                }
            }
            switch (menuItem.getItemId()) {
                case R.id.tab_3: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ly_home, new FragmentHome())
                            .commit();
                    return true;
                }
            }
            switch (menuItem.getItemId()) {
                case R.id.tab_4: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ly_home, new FragmentHome())
                            .commit();
                    return true;
                }
            }
            switch (menuItem.getItemId()) {
                case R.id.tab_5: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.ly_home, new FragmentHome())
                            .commit();
                    return true;
                }
            }
            return false;
        }
    }

    // 프래그먼트를 교체하는 메서드
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ly_home, fragment);
        fragmentTransaction.commit();
    }

}
