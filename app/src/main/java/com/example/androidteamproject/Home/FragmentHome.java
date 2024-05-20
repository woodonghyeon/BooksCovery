package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.util.Log;
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

import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.R;
import com.example.androidteamproject.Search.LatelySearchBook;
import com.example.androidteamproject.Search.SearchPageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragmentHome extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private final int currentEventNum = 4;
    private static final float MIN_SCALE = 0.75f; // WeekBook scale
    long now = System.currentTimeMillis();
    private Date mDate = new Date(now);
    private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    private ViewPager2 currentEventPager, weekBookPager;
    private FragmentStateAdapter homePagerAdapter, weekBookAdapter;
    private TextView tv_department_title, tv_popular_book_week, tv_popular_book_month, tv_book_rental;
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
        CurrentEventSettingImg(view);
        getResponseApiLoanItems();

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

    // 최근 많이 검색된 도서 이미지 출력 (현재는 많이 대출된 도서로 출력함 -> 수정 예정)
    private void getResponseApiLoanItems() {
        String getTime = mFormat.format(mDate); // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance(); // 1주일 전 날짜 가져오기
        calendar.setTime(mDate);
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        String startDt = mFormat.format(calendar.getTime()); // 시작 날짜 (예시)
        String endDt = getTime; // 종료 날짜 (예시)
        int pageNo = 1; // 페이지 번호 (예시)
        int pageSize = 10; // 페이지 크기 (예시)
        String format = "json"; // 응답 형식 (예시)

        HttpConnection.getInstance(getContext()).getLoanItems(startDt, endDt, pageNo, pageSize, format, new HttpConnection.HttpResponseCallback<List<LatelySearchBook>>() {
            @Override
            public void onSuccess(List<LatelySearchBook> books) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        List<String> imageUrls = new ArrayList<>();
                        List<String> bookName = new ArrayList<>();
                        for (LatelySearchBook book : books) {
                            imageUrls.add(book.getBookImageUrl());
                            bookName.add(book.getBookName());
                        }
                        // ViewPager2에 이미지 추가
                        setupViewPager(bookName, imageUrls);
                        Log.d("API Response", "Image URLs: " + imageUrls.toString() + ", BookName: " + bookName.toString());
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 에러 처리 로직 추가
                        Log.e("API Failure", "Error: " + e.getMessage());
                    });
                }
            }
        });
    }

    // ViewPager2 Setting
    private void setupViewPager(List<String> bookName, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 URL이 없는 경우 처리
            return;
        }
        // 이미지 URL이 있을 경우 뷰페이저 설정
        if (getView() == null) {
            return;
        }
        weekBookPager = getView().findViewById(R.id.week_book_viewpager);
        weekBookAdapter = new WeekBookAdapter(requireActivity(), bookName, imageUrls);
        weekBookPager.setAdapter(weekBookAdapter);
        weekBookPager.setCurrentItem(1000);
        weekBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        weekBookPager.setCurrentItem(startPos);

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
                View textView = page.findViewById(R.id.tv_weekBook);
                if(imageView != null) {
                    page.setTranslationX(position * -offsetPx);
                    if (position < -1) return;
                    if (position <= 1) {
                        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position * getEase(Math.abs(position))));
                        imageView.setScaleX(scaleFactor);
                        imageView.setScaleY(scaleFactor);
                        textView.setScaleX(scaleFactor);
                        textView.setScaleY(scaleFactor);
                    } else {
                        imageView.setScaleX(MIN_SCALE);
                        imageView.setScaleY(MIN_SCALE);
                        textView.setScaleX(MIN_SCALE);
                        textView.setScaleY(MIN_SCALE);
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
}
