package com.example.androidteamproject.History;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import com.example.androidteamproject.Home.FragmentBookDetail;
import com.example.androidteamproject.SessionManager;
import com.example.androidteamproject.R;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    private String id;
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
        favorite.setOnClickListener(view1 -> {check = 0; new getHistoryForDB().execute();});
        search.setOnClickListener(view1 -> {check = 1; new getHistoryForDB().execute();});

        // 세션 데이터 가져오기
        Context context = getContext();
        SessionManager sessionManager = new SessionManager(context);
        id = String.valueOf(sessionManager.getId());

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

    // 데이터베이스에서 기록을 검색하는 AsyncTask
    public class getHistoryForDB extends AsyncTask<Void, Void, List<String>[]> {
        int member_id;

        @Override
        protected List<String>[] doInBackground(Void... voids) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            String sql = "";

            // 임시 목록에 데이터 저장
            List<String> booknameTemp = new ArrayList<>();
            List<String> authorsTemp = new ArrayList<>();
            List<String> publisherTemp = new ArrayList<>();
            List<String> bookImageURLTemp = new ArrayList<>();
            List<String> isbn13Temp = new ArrayList<>();

            try {
                // JDBC 드라이버 로드
                Class.forName("com.mysql.jdbc.Driver");
                // 데이터베이스 연결
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

                // 회원 ID를 찾기 위한 쿼리 실행
                sql = "Select * from member_info where id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id);
                rs = pstmt.executeQuery();

                // 회원 ID 가져오기
                if (rs.next()) {
                    member_id = rs.getInt("member_id");
                }

                // 체크 값에 따라 쿼리 실행 (0은 즐겨찾기, 1은 검색 기록)
                if(check == 0){
                    sql = "Select * from favorite f join book b on f.book_id = b.book_id where f.member_id = ?";
                }
                else if(check == 1){
                    sql = "Select * from search_history sh join book b on sh.book_id = b.book_id where sh.member_id = ? order by sh.search_history_id desc";
                }

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, member_id);
                rs = pstmt.executeQuery();

                // 데이터 가져오고 임시 목록에 채우기
                while (rs.next()) {
                    booknameTemp.add(rs.getString("bookname"));
                    authorsTemp.add(rs.getString("authors"));
                    publisherTemp.add(rs.getString("publisher"));
                    bookImageURLTemp.add(rs.getString("book_image_URL"));
                    isbn13Temp.add(rs.getString("isbn"));
                }
                // 리소스 닫기
                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // 가져온 데이터를 포함하는 목록 배열 반환
            return new List[]{booknameTemp, authorsTemp, publisherTemp, bookImageURLTemp, isbn13Temp};
        }

        @Override
        protected void onPostExecute(List<String>[] result) {
            super.onPostExecute(result);

            // 어댑터에 데이터 변경 알리기
            if (adapter != null) {
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

                adapter.notifyDataSetChanged();
            }
        }
    }
}
