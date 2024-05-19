package com.example.androidteamproject.Search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;
import java.util.List;

public class FragmentSearch extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int num_page = 4;
    private String mParam1;
    private String mParam2;
    private ViewPager2 mPager;
    private FragmentStateAdapter searchPagerAdapter;
    private List<String> chipTexts = Arrays.asList("1","2","3","4","5");
    private TextView text;

    public FragmentSearch() {
    }

    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        SettingImg(view);
        addChips(view); // addChips 메소드 호출
        getResponseApiData(view); // API 응답 데이터 가져오기
        return view;
    }

    private void SettingImg(View view) {
        // 가로 슬라이드 뷰 Fragment

        // 첫 번째 ViewPager2
        mPager = view.findViewById(R.id.search_viewpager);
        searchPagerAdapter = new SearchPageAdapter(requireActivity(), num_page);
        mPager.setAdapter(searchPagerAdapter);
        mPager.setCurrentItem(1000);
        mPager.setOffscreenPageLimit(4);

        // viewpager2 간격 변환을 위함 -> res.values.dimes.xml에서 확인
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.searchPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.searchPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        // viewpager2 간격 변환
        mPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));

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
    }
    private void getResponseApiData(View view) {
        text = view.findViewById(R.id.text);

        if (text != null) {
            HttpConnection.getInstance(getContext()).getKeyword("json", new HttpConnection.HttpResponseCallback() {
                @Override
                public void onSuccess(String responseData) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (text != null) {
                                text.setText(responseData);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (text != null) {
                                text.setText("Failed to get data: " + e.getMessage());
                            }
                        });
                    }
                }
            });
        } else {
            // resultTextView가 null인 경우 처리할 로직 추가
        }
    }

    private void addChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chip_group);

        for (String text : chipTexts) {
            Chip chip = new Chip(requireContext());
            chip.setText(text);
            chip.setChipBackgroundColorResource(R.color.brandcolor1); // 칩 배경 색 설정
            chip.setTextColor(getResources().getColor(R.color.primaryDarkColor)); // 텍스트 색상 설정
            chip.setChipStrokeColorResource(R.color.primaryDarkColor); // 칩 테두리 색상 설정
            chip.setChipStrokeWidth(1.0f); // 칩 테두리 두께 설정
            chipGroup.addView(chip); // 칩을 ChipGroup에 추가
        }
    }
}
