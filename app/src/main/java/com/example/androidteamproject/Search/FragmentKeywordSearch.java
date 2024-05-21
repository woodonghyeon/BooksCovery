package com.example.androidteamproject.Search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidteamproject.R;

public class FragmentKeywordSearch extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tv_booksearch;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyword_search, container, false);
        tv_booksearch = view.findViewById(R.id.tv_booksearch);

        // 전달된 키워드를 TextView에 설정
        if (mParam1 != null) {
            tv_booksearch.setText("'" + mParam1 + "' 으로 검색한 도서");
        }
        return view;
    }
}