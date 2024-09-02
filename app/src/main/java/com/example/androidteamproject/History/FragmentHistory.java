package com.example.androidteamproject.History;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidteamproject.ApiData.DataBase; // 서버와 통신하기 위해 DataBase 클래스 사용
import com.example.androidteamproject.Home.FragmentBookDetail;
import com.example.androidteamproject.SessionManager;
import com.example.androidteamproject.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentHistory extends Fragment {

    // 프래그먼트에 전달할 인수
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // 프래그먼트 매개변수
    private String mParam1;
    private String mParam2;

    // 책 정보를 저장할 목록
    private List<String> bookImageURL;
    private List<String> bookname;
    private List<String> authors;
    private List<String> publisher;
    private List<String> searchDate;
    private List<String> isbn13;

    // 회원 ID와 체크 변수
    private int memberId;
    private int check;

    // 뷰와 어댑터
    private ListView list;
    private CustomList adapter;
    private Button favorite, search;

    // 프래그먼트의 새 인스턴스 생성하는 메서드
    public static FragmentHistory newInstance(String param1, String param2) {
        FragmentHistory fragment = new FragmentHistory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 전달된 인수 확인
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        list = view.findViewById(R.id.List);

        // 데이터 초기화
        bookImageURL = new ArrayList<>();
        bookname = new ArrayList<>();
        authors = new ArrayList<>();
        publisher = new ArrayList<>();
        searchDate = new ArrayList<>();
        isbn13 = new ArrayList<>();

        // 어댑터 생성 및 설정
        adapter = new CustomList(this, bookname, bookImageURL, authors, publisher, searchDate);
        list.setAdapter(adapter);

        // 버튼 초기화 및 클릭 리스너 설정
        favorite = view.findViewById(R.id.favorite);
        search = view.findViewById(R.id.search);
        favorite.setOnClickListener(view1 -> {
            check = 0;
            fetchFavoritesFromServer();  // 서버로부터 즐겨찾기 가져오기
        });
        search.setOnClickListener(view1 -> {
            check = 1;
            fetchSearchHistoryFromServer();  // 서버로부터 검색 기록 가져오기
        });

        // 세션 데이터 가져오기
        Context context = getContext();
        SessionManager sessionManager = new SessionManager(context);
        memberId = sessionManager.getMemberId();

        // 기록에서 이미지 누르면 상세보기로 넘어가기
        list.setOnItemClickListener((parent, view1, position, id) -> {
            FragmentBookDetail fragment = FragmentBookDetail.newInstance(isbn13.get(position), bookname.get(position), authors.get(position), bookImageURL.get(position));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.ly_home);
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.ly_home, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    // 서버에서 즐겨찾기 가져오기
    private void fetchFavoritesFromServer() {
        DataBase db = new DataBase();
        db.getFavoriteAll(memberId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "서버 요청 실패", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    List<String>[] result = parseBookData(jsonResponse); // JSON 데이터를 파싱하여 리스트 배열로 변환
                    updateUIWithData(result); // UI 업데이트
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "서버 오류", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // 서버에서 검색 기록 가져오기
    private void fetchSearchHistoryFromServer() {
        DataBase db = new DataBase();
        db.getSearchHistoryAll(memberId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "서버 요청 실패", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    List<String>[] result = parseBookData(jsonResponse); // JSON 데이터를 파싱하여 리스트 배열로 변환
                    updateUIWithData(result); // UI 업데이트
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "서버 오류", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // JSON 데이터를 파싱하여 리스트 배열로 변환하는 메서드
    private List<String>[] parseBookData(String jsonResponse) {
        List<String> booknameTemp = new ArrayList<>();
        List<String> authorsTemp = new ArrayList<>();
        List<String> publisherTemp = new ArrayList<>();
        List<String> bookImageURLTemp = new ArrayList<>();
        List<String> isbn13Temp = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                booknameTemp.add(jsonObject.optString("bookname", ""));
                authorsTemp.add(jsonObject.optString("authors", ""));
                publisherTemp.add(jsonObject.optString("publisher", ""));
                bookImageURLTemp.add(jsonObject.optString("book_image_URL", ""));
                isbn13Temp.add(jsonObject.optString("isbn", ""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new List[]{booknameTemp, authorsTemp, publisherTemp, bookImageURLTemp, isbn13Temp};
    }

    // UI 업데이트 메서드
    private void updateUIWithData(List<String>[] result) {
        getActivity().runOnUiThread(() -> {
            // 기존 데이터 지우기
            bookname.clear();
            authors.clear();
            publisher.clear();
            bookImageURL.clear();
            isbn13.clear();

            // 새 데이터 추가
            bookname.addAll(result[0]);
            authors.addAll(result[1]);
            publisher.addAll(result[2]);
            bookImageURL.addAll(result[3]);
            isbn13.addAll(result[4]);

            adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알리기
        });
    }

    // 리스트 뷰의 커스텀 어댑터
    public class CustomList extends ArrayAdapter<String> {
        private FragmentHistory context;

        public CustomList(FragmentHistory context, List<String> bookNames, List<String> bookImages, List<String> bookAuthors, List<String> bookPublishers, List<String> bookSearchDates) {
            super(context.getActivity(), R.layout.history_item, bookNames);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                view = inflater.inflate(R.layout.history_item, parent, false);
            }

            // 리스트 항목 레이아웃에서 뷰 찾기
            TextView nameView = view.findViewById(R.id.bookname);
            TextView authorView = view.findViewById(R.id.authors);
            TextView publisherView = view.findViewById(R.id.publisher);
            ImageView imageView = view.findViewById(R.id.image);

            // 뷰에 데이터 설정
            nameView.setText(bookname.get(position));
            authorView.setText(authors.get(position));
            publisherView.setText(publisher.get(position));

            // Picasso 라이브러리를 사용하여 이미지 로드
            String imageUrl = bookImageURL.get(position);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_error).error(R.drawable.ic_error).into(imageView);
            } else {
                Picasso.get().load(R.drawable.ic_error).into(imageView);
            }

            return view;
        }
    }
}
