package com.example.androidteamproject.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBook;
import com.example.androidteamproject.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentSearch extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private ViewPager2 mPager;
    private FragmentStateAdapter searchPagerAdapter;
    private List<String> keywords = new ArrayList<>();
    private List<String> search_word = new ArrayList<>(Arrays.asList("테스트용 검색어1" , "테스트용 검색어2" , "테스트용 검색어3" , "테스트용 검색어4" , "테스트용 검색어5" , "테스트용 검색어6" , "테스트용 검색어7" , "테스트용 검색어8" , "테스트용 검색어9" , "테스트용 검색어10"));

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
                            addChips(false); // 처음에는 제한된 수만큼의 칩 추가
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

    private void addChips(boolean showAll) {
        if (getContext() == null) return;
        ChipGroup chipGroup1 = requireView().findViewById(R.id.chip_group1);
        ChipGroup chipGroup2 = requireView().findViewById(R.id.chip_group2);

        // 다른 이벤트가 발생하면 중복적으로 chip이 발생하여 일부로 한번 지우고 다시 세팅하는걸로 넣은거임
        chipGroup1.removeAllViews();
        chipGroup2.removeAllViews();

        // 키워드를 이용하여 칩 추가
        int keywordLimit = showAll ? keywords.size() : Math.min(20, keywords.size());

        for (int i = 0; i < keywordLimit; i++) {
            final String keyword = keywords.get(i); // i 값을 복사하여 final 변수에 저장
            Chip chip = new Chip(requireContext());
            chip.setText(keyword);
            chip.setChipBackgroundColorResource(R.color.brandcolor1); // 칩 배경 색 설정
            chip.setTextColor(getResources().getColor(R.color.primaryDarkColor)); // 텍스트 색상 설정
            chip.setChipStrokeColorResource(R.color.primaryDarkColor); // 칩 테두리 색상 설정
            chip.setChipStrokeWidth(1.0f); // 칩 테두리 두께 설정

            // 칩 클릭 이벤트 추가
            chip.setOnClickListener(v -> onChipClick(keyword));

            chipGroup1.addView(chip); // 칩을 ChipGroup에 추가
        }

        if (!showAll && keywords.size() > 20) {
            Chip moreChip = new Chip(requireContext());
            moreChip.setText("더보기...");
            moreChip.setChipBackgroundColorResource(R.color.brandcolor1); // 칩 배경 색 설정
            moreChip.setTextColor(getResources().getColor(R.color.primaryDarkColor)); // 텍스트 색상 설정
            moreChip.setChipStrokeColorResource(R.color.primaryDarkColor); // 칩 테두리 색상 설정
            moreChip.setChipStrokeWidth(1.0f); // 칩 테두리 두께 설정
            moreChip.setOnClickListener(v -> addChips(true)); // "더보기" 클릭 시 모든 키워드 표시
            chipGroup1.addView(moreChip); // "더보기" 칩 추가
        }

        // 검색어를 이용하여 칩 추가 (나중에 DB에서 검색어를 가져와야함)
        for (String word : search_word) {
            Chip chip = new Chip(requireContext());
            chip.setText(word);
            chip.setChipBackgroundColorResource(R.color.brandcolor1); // 칩 배경 색 설정
            chip.setTextColor(getResources().getColor(R.color.primaryDarkColor)); // 텍스트 색상 설정
            chip.setChipStrokeColorResource(R.color.primaryDarkColor); // 칩 테두리 색상 설정
            chip.setChipStrokeWidth(1.0f); // 칩 테두리 두께 설정

            // 칩 클릭 이벤트 추가
            chip.setOnClickListener(v -> onChipClick(word));

            chipGroup2.addView(chip); // 칩을 ChipGroup에 추가
        }
    }

    // 칩 클릭 이벤트 처리 메서드
    private void onChipClick(String keyword) {
        // FragmentKeywordSearch로 이동
        FragmentKeywordSearch fragment = FragmentKeywordSearch.newInstance(keyword, ""); // 두 번째 인자는 필요에 따라 변경
        getParentFragmentManager().beginTransaction()
                .replace(R.id.ly_home, fragment) // R.id.fragment_container는 실제로 프래그먼트를 교체할 컨테이너 ID로 대체
                .addToBackStack(null) // 백스택에 추가하여 뒤로가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있음
                .commit();
    }

    // 최근 많이 검색된 도서 이미지 출력 (현재는 많이 대출된 도서로 출력함 -> 수정 예정)
    private void getResponseApiLoanItems() {
        String startDt = "2023-01-01"; // 시작 날짜 (예시)
        String endDt = "2024-05-01"; // 종료 날짜 (예시)
        String from_age = "20"; //최소 나이
        String to_age = "40"; // 최대 나이
        int pageNo = 1; // 페이지 번호 (예시)
        int pageSize = 10; // 페이지 크기 (예시)
        String format = "json"; // 응답 형식 (예시)

        HttpConnection.getInstance(getContext()).getLoanItems(startDt, endDt, from_age, to_age, pageNo, pageSize, format, new HttpConnection.HttpResponseCallback<List<SearchBook>>() {
            @Override
            public void onSuccess(List<SearchBook> books) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        List<String> imageUrls = new ArrayList<>();
                        List<String> bookName = new ArrayList<>();
                        for (SearchBook book : books) {
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
        mPager = getView().findViewById(R.id.search_viewpager);
        searchPagerAdapter = new SearchPageAdapter(requireActivity(), bookName, imageUrls);
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
