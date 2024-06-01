package com.example.androidteamproject.Search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBook;
import com.example.androidteamproject.Home.FragmentBookDetail;
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
    private SearchView sv_keyword, sv_title, sv_author;
    private Animation anime_left_to_right, anime_right_to_left;

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
        sv_title = view.findViewById(R.id.sv_title);
        sv_author = view.findViewById(R.id.sv_author);
        sv_keyword = view.findViewById(R.id.sv_keyword);

        // SearchView의 힌트 텍스트 색상을 파란색으로 설정
        setSearchViewHintColor(sv_title, R.color.brandcolor2);
        setSearchViewHintColor(sv_author, R.color.brandcolor2);
        setSearchViewHintColor(sv_keyword, R.color.brandcolor2);

        sv_title.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByTitle(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        sv_author.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchByAuthor(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        sv_keyword.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 키워드 입력 후 검색 버튼을 누르면 searchByKeyword 메서드 호출
                searchByKeyword(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startAnimation(view);

        // SharedPreferences에서 키워드를 불러옴
        keywords = loadKeywordsFromSharedPreferences();

        // 키워드가 비어있거나 시간이 지난 경우 API 호출
        if (keywords.isEmpty() || timeCheck()) {
            getResponseApiKeyword();
        } else {
            // SharedPreferences에서 불러온 키워드를 사용하여 칩 추가
            addChips();
        }
    }

    private void startAnimation(View view) {
        TextView tv_Keyword_of_the_month = view.findViewById(R.id.tv_Keyword_of_the_month);

        anime_left_to_right = AnimationUtils.loadAnimation(getContext(), R.anim.anime_left_to_right);
        anime_right_to_left = AnimationUtils.loadAnimation(getContext(), R.anim.anime_right_to_left);

        tv_Keyword_of_the_month.startAnimation(anime_right_to_left);
    } // end of startAnimation

    // SearchView의 힌트 텍스트 색상을 변경하는 메서드
    private void setSearchViewHintColor(SearchView searchView, int colorResId) {
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        int color = ContextCompat.getColor(getContext(), colorResId);
        searchEditText.setHintTextColor(color);
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

    // showBookDetail
    private void showBookDetail(String isbn13, String bookName, String authors, String imageUrl) {
        // 새로운 FragmentBookDetail 인스턴스를 생성하고 필요한 데이터를 전달
        FragmentBookDetail fragment = FragmentBookDetail.newInstance(isbn13, bookName, authors, imageUrl);

        // FragmentTransaction을 통해 프래그먼트를 관리
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // 현재 프래그먼트를 가져와서 숨김
        Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.ly_home);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // 새로운 프래그먼트를 추가
        transaction.add(R.id.ly_home, fragment);
        transaction.addToBackStack(null); // 백스택에 추가하여 뒤로가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있음
        transaction.commit();
    }

    // SharedPreferences에서 키워드를 불러오는 메서드
    private List<String> loadKeywordsFromSharedPreferences() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("keywords", Context.MODE_PRIVATE);
        String keywordString = preferences.getString("keywords", "");
        List<String> keywords = new ArrayList<>();

        if (!keywordString.isEmpty()) {
            String[] keywordArray = keywordString.split(",");
            for (String keyword : keywordArray) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }

    // SharedPreferences에 키워드를 저장하는 메서드
    private void saveKeywordsToSharedPreferences(List<String> keywords) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("keywords", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        StringBuilder keywordString = new StringBuilder();

        for (String keyword : keywords) {
            keywordString.append(keyword).append(",");
        }

        if (keywordString.length() > 0) {
            keywordString.setLength(keywordString.length() - 1); // 마지막 쉼표 제거
        }

        editor.putString("keywords", keywordString.toString());
        editor.apply();
    }

    // 날짜 차이를 확인하여 시간이 지난 경우 true를 반환하는 메서드
    private boolean timeCheck() {
        if (checkDate == null || mDate.minusDays(7).isAfter(checkDate)) {
            checkDate = mDate;
            return true;
        }
        return false;
    }

    // 키워드 검색 메서드
    private void searchByKeyword(String keyword) {
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

    // 타이틀 검색
    private void searchByTitle(String title) {
        FragmentTitleSearch fragment = FragmentTitleSearch.newInstance(title, "");

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

    // 저자 검색
    private void searchByAuthor(String author) {
        FragmentAuthorSearch fragment = FragmentAuthorSearch.newInstance(author, "");

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
