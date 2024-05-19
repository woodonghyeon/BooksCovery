package com.example.androidteamproject.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearch extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int num_page = 4;
    private String mParam1;
    private String mParam2;
    private ViewPager2 mPager;
    private FragmentStateAdapter searchPagerAdapter;
    private List<String> keywords = new ArrayList<>();

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
        getResponseApiKeyword(); // API 응답 데이터 가져오기
        getResponseApiLoanItems();
        return view;
    }

    private void getResponseApiKeyword() {
        HttpConnection.getInstance(getContext()).getKeyword("json", new HttpConnection.HttpResponseCallback() {
            @Override
            public void onSuccess(Object responseData) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(responseData.toString());
                            JSONObject responseObject = json.getJSONObject("response");
                            JSONArray keywordsArray = responseObject.getJSONArray("keywords");

                            for (int i = 0; i < keywordsArray.length(); i++) {
                                JSONObject keywordObject = keywordsArray.getJSONObject(i);
                                JSONObject keyword = keywordObject.getJSONObject("keyword");
                                String word = keyword.getString("word");
                                keywords.add(word);
                            }
                            // 키워드를 가져온 후 칩 추가
                            addChips();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 에러 처리 로직 추가
                    });
                }
            }
        });
    }


    private void addChips() {
        if (getContext() == null) return;
        ChipGroup chipGroup = requireView().findViewById(R.id.chip_group);

        // 기존 칩들을 모두 제거
        chipGroup.removeAllViews();

        // 키워드를 이용하여 칩 추가
        for (String keyword : keywords) {
            Chip chip = new Chip(requireContext());
            chip.setText(keyword);
            chip.setChipBackgroundColorResource(R.color.brandcolor1); // 칩 배경 색 설정
            chip.setTextColor(getResources().getColor(R.color.primaryDarkColor)); // 텍스트 색상 설정
            chip.setChipStrokeColorResource(R.color.primaryDarkColor); // 칩 테두리 색상 설정
            chip.setChipStrokeWidth(1.0f); // 칩 테두리 두께 설정
            chipGroup.addView(chip); // 칩을 ChipGroup에 추가
        }
    }
    
    // 최근 많이 검색된 도서 이미지 출력 (현재는 많이 대출된 도서로 출력함 -> 수정 예정)
    private void getResponseApiLoanItems() {
        String startDt = "2023-01-01"; // 시작 날짜 (예시)
        String endDt = "2024-05-01"; // 종료 날짜 (예시)
        int pageNo = 1; // 페이지 번호 (예시)
        int pageSize = 10; // 페이지 크기 (예시)
        String format = "json"; // 응답 형식 (예시)

        HttpConnection.getInstance(getContext()).getLoanItems(startDt, endDt, pageNo, pageSize, format, new HttpConnection.HttpResponseCallback<List<LatelySearchBook>>() {
            @Override
            public void onSuccess(List<LatelySearchBook> books) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        List<String> imageUrls = new ArrayList<>();
                        for (LatelySearchBook book : books) {
                            imageUrls.add(book.getBookImageUrl());
                        }
                        // ViewPager2에 이미지 추가
                        setupViewPager(imageUrls);
                        Log.d("API Response", "Image URLs: " + imageUrls.toString());
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
    private void setupViewPager(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 URL이 없는 경우 처리
            return;
        }
        // 이미지 URL이 있을 경우 뷰페이저 설정
        if (getView() == null) {
            return;
        }
        mPager = getView().findViewById(R.id.search_viewpager);
        searchPagerAdapter = new SearchPageAdapter(requireActivity(), imageUrls);
        mPager.setAdapter(searchPagerAdapter);
        mPager.setCurrentItem(1000);
        mPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        mPager.setCurrentItem(startPos);

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
}
