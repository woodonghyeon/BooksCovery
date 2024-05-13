package com.example.androidteamproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class FragmentHome extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int num_page = 10;
    private String mParam1;
    private String mParam2;
    private ViewPager2 mPager, mPager2, mPager3;
    private FragmentStateAdapter pagerAdapter;
    private TextView tv_department_title, tv_popular_book_week, tv_popular_book_month;
    private Animation anime_left_to_right, anime_right_to_left;

    public FragmentHome() {
    }

    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        startAnimation(view);
        SettingImg(view);
        return view;
    }

    private void SettingImg(View view) {
        /**
         * 가로 슬라이드 뷰 Fragment
         */

        // 첫 번째 ViewPager2
        mPager = view.findViewById(R.id.viewpager);
        pagerAdapter = new MyAdapter(requireActivity(), num_page);
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(1000);
        mPager.setOffscreenPageLimit(4);
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        // 두 번째 ViewPager2
        mPager2 = view.findViewById(R.id.viewpager2);
        pagerAdapter = new MyAdapter(requireActivity(), num_page); // 같은 어댑터 재사용
        mPager2.setAdapter(pagerAdapter);
        mPager2.setCurrentItem(1000);
        mPager2.setOffscreenPageLimit(4);
        mPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager2.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        // 세 번째 ViewPager2
        mPager3 = view.findViewById(R.id.viewpager3);
        pagerAdapter = new MyAdapter(requireActivity(), num_page); // 같은 어댑터 재사용
        mPager3.setAdapter(pagerAdapter);
        mPager3.setCurrentItem(1000);
        mPager3.setOffscreenPageLimit(4);
        mPager3.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager3.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    }

    // 타이틀 애니메이션
    private void startAnimation(View view) {
        tv_department_title = view.findViewById(R.id.tv_department_title);
        tv_popular_book_week = view.findViewById(R.id.tv_popular_book_week);
        tv_popular_book_month = view.findViewById(R.id.tv_popular_book_month);

        anime_left_to_right = AnimationUtils.loadAnimation(getContext(), R.anim.anime_left_to_right);
        anime_right_to_left = AnimationUtils.loadAnimation(getContext(), R.anim.anime_right_to_left);

        tv_department_title.startAnimation(anime_right_to_left);
        tv_popular_book_week.startAnimation(anime_left_to_right);
        tv_popular_book_month.startAnimation(anime_right_to_left);
    }
}
