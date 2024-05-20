package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.androidteamproject.R;

public class FragmentHome extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private final int currentEventNum = 4;
    private final int weekBookNum = 3;
    private static final float MIN_SCALE = 0.75f; // WeekBook scale

    private ViewPager2 currentEventPager, weekBookPager;
    private FragmentStateAdapter homePagerAdapter;
    private TextView tv_department_title, tv_popular_book_week, tv_popular_book_month, tv_book_rental;
    private Animation anime_left_to_right, anime_right_to_left;
    private TextView resultTextView;

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
        CurrentEventSettingImg(view);
        WeekBookSettingImg(view);
//        getResponseApiData(view);

        return view;
    }

    private void CurrentEventSettingImg(View view) {
        // 가로 슬라이드 뷰 Fragment

        // 첫 번째 ViewPager2
        currentEventPager = view.findViewById(R.id.event_viewpager);
        homePagerAdapter = new CurrentEventAdapter(requireActivity(), currentEventNum);
        currentEventPager.setAdapter(homePagerAdapter);
        currentEventPager.setCurrentItem(1000);
        currentEventPager.setOffscreenPageLimit(4);

        // viewpager2 간격 변환을 위함 -> res.values.dimes.xml에서 확인
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.eventPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.eventPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        // viewpager2 간격 변환
        currentEventPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setTranslationX(position * -offsetPx);
            }
        });

        currentEventPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    currentEventPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    }

    private void WeekBookSettingImg(View view) {
        // 가로 슬라이드 뷰 Fragment

        // 첫 번째 ViewPager2
        weekBookPager = view.findViewById(R.id.week_book_viewpager);
        homePagerAdapter = new WeekBookAdapter(requireActivity(), weekBookNum);
        weekBookPager.setAdapter(homePagerAdapter);
        weekBookPager.setCurrentItem(1000);
        weekBookPager.setOffscreenPageLimit(3);

        // viewpager2 간격 변환을 위함 -> res.values.dimes.xml에서 확인
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.weekBookPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.weekBookPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        // viewpager2 간격 변환
        weekBookPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                View imageView = page.findViewById(R.id.iv_weekBookImg);
                if(imageView != null) {
                    page.setTranslationX(position * -offsetPx);
                    if (position < -1) return;
                    if (position <= 1) {
                        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position * getEase(Math.abs(position))));
                        imageView.setScaleX(scaleFactor);
                        imageView.setScaleY(scaleFactor);
                    } else {
                        imageView.setScaleX(MIN_SCALE);
                        imageView.setScaleY(MIN_SCALE);
                    }
                }
            }

            private float getEase(float position) {
                float sqt = position * position;
                return sqt / (2.0f * (sqt - position) + 1.0f);
            }
        });

        weekBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    weekBookPager.setCurrentItem(position);
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
        tv_department_title = view.findViewById(R.id.tv_currentEvent);
        tv_popular_book_week = view.findViewById(R.id.tv_popular_book_week);
        tv_popular_book_month = view.findViewById(R.id.tv_popular_book_month);
        tv_book_rental = view.findViewById(R.id.tv_book_rental);

        anime_left_to_right = AnimationUtils.loadAnimation(getContext(), R.anim.anime_left_to_right);
        anime_right_to_left = AnimationUtils.loadAnimation(getContext(), R.anim.anime_right_to_left);

        tv_department_title.startAnimation(anime_right_to_left);
        tv_popular_book_week.startAnimation(anime_right_to_left);
        tv_popular_book_month.startAnimation(anime_right_to_left);
        tv_book_rental.startAnimation(anime_left_to_right);
    }

//    // API Data 받아오는 부분 Test
//    private void getResponseApiData(View view) {
//        resultTextView = view.findViewById(R.id.resultTextView);
//
//        if (resultTextView != null) {
//            HttpConnection.getInstance(getContext()).getLibraries(1, 2, "json", new HttpConnection.HttpResponseCallback() {
//                @Override
//                public void onSuccess(String responseData) {
//                    if (getActivity() != null) {
//                        getActivity().runOnUiThread(() -> {
//                            if (resultTextView != null) {
//                                resultTextView.setText(responseData);
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    if (getActivity() != null) {
//                        getActivity().runOnUiThread(() -> {
//                            if (resultTextView != null) {
//                                resultTextView.setText("Failed to get data: " + e.getMessage());
//                            }
//                        });
//                    }
//                }
//            });
//        } else {
//            // resultTextView가 null인 경우 처리할 로직 추가
//        }
//    }
}
