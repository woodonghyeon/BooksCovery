package com.example.androidteamproject.Search;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBookKeyword;
import com.example.androidteamproject.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentKeywordSearch extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView tv_booksearch;
    private ListView listView;
    private BookListAdapter adapter;
    private List<SearchBookKeyword> bookList;

    public FragmentKeywordSearch() {
        // Required empty public constructor
    }

    public static FragmentKeywordSearch newInstance(String param1, String param2) {
        FragmentKeywordSearch fragment = new FragmentKeywordSearch();
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
        View view = inflater.inflate(R.layout.fragment_keyword_search, container, false);
        tv_booksearch = view.findViewById(R.id.tv_booksearch);
        listView = view.findViewById(R.id.listView);

        // 전달된 키워드를 TextView에 설정
        if (mParam1 != null) {
            tv_booksearch.setText("'" + mParam1 + "' 으로 검색한 도서");
        }

        // 리스트뷰를 위한 어댑터 설정
        bookList = new ArrayList<>();
        adapter = new BookListAdapter(getContext(), bookList);
        listView.setAdapter(adapter);

        // API 데이터 가져오기
        getResponseApiBookSearch();

        return view;
    }

    private void getResponseApiBookSearch() {
        String keyword = mParam1;
        int pageNo = 1;
        int pageSize = 10;
        String format = "json";

        HttpConnection.getInstance(getContext()).bookSearch(keyword, pageNo, pageSize, format, new HttpConnection.HttpResponseCallback<List<SearchBookKeyword>>() {
            @Override
            public void onSuccess(List<SearchBookKeyword> books) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        bookList.clear(); // 기존 데이터 초기화
                        bookList.addAll(books); // 새로운 데이터 추가
                        adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
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
}
