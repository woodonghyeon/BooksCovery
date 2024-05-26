package com.example.androidteamproject.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBookDetail;
import com.example.androidteamproject.ApiData.SearchBookTitle;
import com.example.androidteamproject.Home.FragmentBookDetail;
import com.example.androidteamproject.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentTitleSearch extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView tv_booksearch;
    private ListView listView;
    private BookListTitleAdapter adapter;
    private List<SearchBookTitle> bookList;

    public FragmentTitleSearch() {
        // 기본 생성자
    }

    public static FragmentTitleSearch newInstance(String param1, String param2) {
        FragmentTitleSearch fragment = new FragmentTitleSearch();
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
        View view = inflater.inflate(R.layout.fragment_title_search, container, false);
        tv_booksearch = view.findViewById(R.id.tv_booksearch);
        listView = view.findViewById(R.id.listView);

        // 전달된 검색어를 TextView에 설정
        if (mParam1 != null) {
            tv_booksearch.setText("'" + mParam1 + "' 으로 검색한 도서");
        }

        // 리스트뷰를 위한 어댑터 설정
        bookList = new ArrayList<>();
        adapter = new BookListTitleAdapter(getContext(), bookList);
        listView.setAdapter(adapter);

        // 리스트뷰 항목 클릭 리스너 설정
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchBookTitle selectedBook = bookList.get(position); // 클릭시 프래그먼트 체인지
                showBookDetail(selectedBook);
            }
        });

        // API 데이터 가져오기
        getResponseApiBookSearch();

        return view;
    }

    private void showBookDetail(SearchBookTitle selectedBook) {
        FragmentBookDetail fragment = FragmentBookDetail.newInstance(
                selectedBook.getIsbn13(),
                selectedBook.getBookName(),
                selectedBook.getAuthors(),
                selectedBook.getBookImageUrl()
        );

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.ly_home, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getResponseApiBookSearch() {
        String title = mParam1;
        int pageNo = 1;
        int pageSize = 100;
        String format = "json";
        boolean tf = true;

        HttpConnection.getInstance(getContext()).bookSearchTitle(title, pageNo, pageSize, tf, format, new HttpConnection.HttpResponseCallback<List<SearchBookTitle>>() {
            @Override
            public void onSuccess(List<SearchBookTitle> books) {
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
