package com.example.androidteamproject.Search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

import java.time.LocalDate;
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
    private static List<String> keywords; //상수로 변경
    private List<String> search_word = new ArrayList<>(Arrays.asList("테스트용 검색어1" , "테스트용 검색어2" , "테스트용 검색어3" , "테스트용 검색어4" , "테스트용 검색어5" , "테스트용 검색어6" , "테스트용 검색어7" , "테스트용 검색어8" , "테스트용 검색어9" , "테스트용 검색어10"));
    @SuppressLint("SimpleDateFormat")
    private static LocalDate mDate = LocalDate.now(); //현재 시각 (static 부여)
    private static LocalDate checkDate; // 기록된 시각 (static 부여)

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // SharedPreferences에서 keywords 불러오기
        keywords = loadKeywordsFromSharedPreferences();

        if (keywords.isEmpty() || timeCheck()) { // 키워드가 비어있거나 시간이 지난 경우에만 API 호출
            getResponseApiKeyword(); // API 응답 데이터 가져오기
        } else {
            addChips(); // SharedPreferences에서 불러온 키워드를 사용하여 칩 추가
        }

        getResponseApiLoanItems();
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
                            keywords = new ArrayList<>(); // 키워드 초기화

                            for (int i = 0; i < keywordsArray.length(); i++) {
                                JSONObject keywordObject = keywordsArray.getJSONObject(i);
                                JSONObject keyword = keywordObject.getJSONObject("keyword");
                                String word = keyword.getString("word");
                                keywords.add(word);
                            }

                            // API 응답을 받아온 후 키워드를 SharedPreferences에 저장
                            saveKeywordsToSharedPreferences(keywords);

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
        ChipGroup chipGroup1 = requireView().findViewById(R.id.chip_group1);
        ChipGroup chipGroup2 = requireView().findViewById(R.id.chip_group2);

        // 다른 이벤트가 발생하면 중복적으로 chip이 발생하여 일부러 한번 지우고 다시 세팅하는걸로 넣은거임
        chipGroup1.removeAllViews();
        chipGroup2.removeAllViews();

        // 키워드를 이용하여 칩 추가
        for (String keyword : keywords) {
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
        FragmentKeywordSearch fragment = FragmentKeywordSearch.newInstance(keyword, ""); // 두 번째 인자는 필요에 따라 변경

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // 현재 프래그먼트를 가져와서 숨김
        Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.ly_home);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // 새 프래그먼트를 추가
        transaction.add(R.id.ly_home, fragment);
        transaction.addToBackStack(null); // 백스택에 추가하여 뒤로가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있음
        transaction.commit();
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

    private boolean timeCheck(){
        //간단 설명
        //기록된 시간, 현재시간을 비교해서 기록된 시간보다 현재시간이 하루 이상 많을 때 api요청
        //만약 아니라면 아직 하루가 안지났기 때문에 최신 데이터임
        // mDate가 현재 시각, checkDate가 기록된 시간
        try{
            int check = mDate.compareTo(checkDate); //시간 비교

            //현재 시간이 기록된 시간보다 높다 => 기록된 키워드는 예전 데이터이다. => API요청해야한다.
            if(check > 0){ return true; }
            else { return false; }
        }
        //만약에 checkDate가 비어있다 => 처음 들어왔다 => API요청해야한다.
        catch (NullPointerException ignored){
            checkDate = mDate; //기록을 현재 시간으로 교체 => 왜냐하면 지금 요청 할꺼니까?
            return true;
        }
    }

    private void saveKeywordsToSharedPreferences(List<String> keywords) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // List<String>를 JSON 문자열로 변환
        JSONArray jsonArray = new JSONArray(keywords);
        String jsonString = jsonArray.toString();

        editor.putString("keywords", jsonString);
        editor.apply();
    }

    private List<String> loadKeywordsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString("keywords", null);

        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                List<String> keywords = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    keywords.add(jsonArray.getString(i));
                }
                return keywords;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(); // 저장된 키워드가 없으면 빈 리스트 반환
    }
}
