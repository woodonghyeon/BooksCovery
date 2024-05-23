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
import java.util.List;
import androidx.appcompat.widget.SearchView;

public class FragmentSearch extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private ViewPager2 mPager;
    private FragmentStateAdapter searchPagerAdapter;
    private static List<String> keywords;
    @SuppressLint("SimpleDateFormat")
    private static LocalDate mDate = LocalDate.now();
    private static LocalDate checkDate;
    private SearchView svKeyword;

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
        // LayoutInflater를 이용하여 fragment_search 레이아웃을 뷰로 변환
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // sv_keyword SearchView를 초기화하고 리스너 설정
        svKeyword = view.findViewById(R.id.sv_keyword);
        svKeyword.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 키워드 입력 후 검색 버튼을 누르면 searchByKeyword 메서드 호출
                searchByKeyword(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 텍스트가 변경될 때마다 호출됨 (여기서는 처리하지 않음)
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // SharedPreferences에서 키워드를 불러옴
        keywords = loadKeywordsFromSharedPreferences();

        // 키워드가 비어있거나 시간이 지난 경우 API 호출
        if (keywords.isEmpty() || timeCheck()) {
            getResponseApiKeyword();
        } else {
            // SharedPreferences에서 불러온 키워드를 사용하여 칩 추가
            addChips();
        }
        // 최근 많이 대출된 도서 이미지 출력
        getResponseApiLoanItems();
    }

    // 키워드 검색 API 호출 메서드
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
                            keywords = new ArrayList<>();

                            // API 응답에서 키워드를 추출하여 리스트에 추가
                            for (int i = 0; i < keywordsArray.length(); i++) {
                                JSONObject keywordObject = keywordsArray.getJSONObject(i);
                                JSONObject keyword = keywordObject.getJSONObject("keyword");
                                String word = keyword.getString("word");
                                keywords.add(word);
                            }

                            // 추출한 키워드를 SharedPreferences에 저장
                            saveKeywordsToSharedPreferences(keywords);

                            // 키워드를 사용하여 칩 추가
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

    // 키워드를 사용하여 칩을 추가하는 메서드
    private void addChips() {
        if (getContext() == null) return;
        ChipGroup chipGroup1 = requireView().findViewById(R.id.chip_group1);

        // 중복 칩 제거를 위해 기존 칩들을 모두 제거
        chipGroup1.removeAllViews();

        // 키워드를 이용하여 칩 추가
        for (String keyword : keywords) {
            Chip chip = new Chip(requireContext());
            chip.setText(keyword);
            chip.setChipBackgroundColorResource(R.color.brandcolor1);
            chip.setTextColor(getResources().getColor(R.color.primaryDarkColor));
            chip.setChipStrokeColorResource(R.color.primaryDarkColor);
            chip.setChipStrokeWidth(1.0f);

            // 칩 클릭 이벤트 추가
            chip.setOnClickListener(v -> onChipClick(keyword));

            chipGroup1.addView(chip);
        }
    }

    // 칩 클릭 이벤트 처리 메서드
    private void onChipClick(String keyword) {
        FragmentKeywordSearch fragment = FragmentKeywordSearch.newInstance(keyword, "");

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

    // 최근 많이 대출된 도서 이미지 출력
    private void getResponseApiLoanItems() {
        String startDt = "2023-01-01";
        String endDt = "2024-05-01";
        String from_age = "20";
        String to_age = "40";
        int pageNo = 1;
        int pageSize = 10;
        String format = "json";

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
                        setupViewPager(bookName, imageUrls);
                        Log.d("API Response", "Image URLs: " + imageUrls.toString() + ", BookName: " + bookName.toString());
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.e("API Failure", "Error: " + e.getMessage());
                    });
                }
            }
        });
    }

    // ViewPager2 설정 메서드
    private void setupViewPager(List<String> bookName, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
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

        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.searchPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.searchPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        mPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));
        mPager.setCurrentItem(mPager.getAdapter().getItemCount() / 2);
    }

    // SharedPreferences에서 키워드를 불러오는 메서드
    private List<String> loadKeywordsFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("keywords", Context.MODE_PRIVATE);
        String keywordsString = sharedPreferences.getString("keywords", "");
        List<String> keywordsList = new ArrayList<>();

        if (!keywordsString.isEmpty()) {
            String[] keywordsArray = keywordsString.split(",");
            for (String keyword : keywordsArray) {
                keywordsList.add(keyword);
            }
        }
        return keywordsList;
    }

    // SharedPreferences에 키워드를 저장하는 메서드
    private void saveKeywordsToSharedPreferences(List<String> keywords) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("keywords", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder keywordsString = new StringBuilder();

        for (int i = 0; i < keywords.size(); i++) {
            keywordsString.append(keywords.get(i));
            if (i < keywords.size() - 1) {
                keywordsString.append(",");
            }
        }

        editor.putString("keywords", keywordsString.toString());
        editor.apply();
    }

    // 시간을 체크하여 일정 시간이 지났는지 확인하는 메서드
    private boolean timeCheck() {
        // 간단 설명: 기록된 시간, 현재 시간을 비교해서 기록된 시간보다 현재 시간이 하루 이상 많을 때 API 요청
        // 만약 아니라면 아직 하루가 안 지났기 때문에 최신 데이터임
        // mDate가 현재 시각, checkDate가 기록된 시간
        try {
            int check = mDate.compareTo(checkDate); // 시간 비교

            // 현재 시간이 기록된 시간보다 높다 => 기록된 키워드는 예전 데이터이다. => API 요청해야 한다.
            if (check > 0) {
                return true;
            } else {
                return false;
            }
        }
        // 만약에 checkDate가 비어있다 => 처음 들어왔다 => API 요청해야 한다.
        catch (NullPointerException ignored) {
            checkDate = mDate; // 기록을 현재 시간으로 교체
            return true;
        }
    }

    // 키워드 검색 기능을 위한 메서드 추가
    private void searchByKeyword(String keyword) {
        // FragmentKeywordSearch를 생성하고 키워드를 전달
        FragmentKeywordSearch fragment = FragmentKeywordSearch.newInstance(keyword, "");

        // FragmentTransaction을 사용하여 프래그먼트를 추가 또는 교체
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
}
