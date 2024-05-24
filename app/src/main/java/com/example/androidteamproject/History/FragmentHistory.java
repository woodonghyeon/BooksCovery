package com.example.androidteamproject.History;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidteamproject.Login.SessionManager;
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

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static List<String> bookImageURL = new ArrayList<>();
    private static List<String> bookname = new ArrayList<>();
    private static List<String> authors = new ArrayList<>();
    private static List<String> publisher = new ArrayList<>();
    private static List<String> searchDate = new ArrayList<>();

    private static String id; //회원 아이디
    private static int check; // 목록 체크 용도 (0이면 즐겨찾기, 1이면 검색기록)

    private ListView list;
    private static CustomList adapter;

    private Button favorite, search;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        adapter = new CustomList(this, bookname, bookImageURL, authors, publisher, searchDate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        list = view.findViewById(R.id.List);

        //버튼 활성화
        favorite = view.findViewById(R.id.favorite);
        search = view.findViewById(R.id.search);

        // 처음에만 DB에서 불러옴
        favorite.setOnClickListener(view1 -> {check = 0; new getHistoryForDB().execute();});
        search.setOnClickListener(view1 -> {check = 1; new getHistoryForDB().execute();});

        // 세션 데이터 가져오기
        Context context = getContext(); // 또는 getActivity()
        SessionManager sessionManager = new SessionManager(context);
        id = String.valueOf(sessionManager.getId());

        list.setAdapter(adapter);
        return view;
    }

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

            TextView nameView = view.findViewById(R.id.bookname);
            TextView authorView = view.findViewById(R.id.authors);
            TextView publisherView = view.findViewById(R.id.publisher);
            ImageView imageView = view.findViewById(R.id.image);

            nameView.setText(bookname.get(position));
            authorView.setText(authors.get(position));
            publisherView.setText(publisher.get(position));
            Picasso.get().load(bookImageURL.get(position)).into(imageView);

            return view;
        }
    }

    public static class getHistoryForDB extends AsyncTask<Void, Void, List<String>[]> {

        int member_id;

        @Override
        protected List<String>[] doInBackground(Void... voids) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            String sql = "";

            List<String> booknameTemp = new ArrayList<>();
            List<String> authorsTemp = new ArrayList<>();
            List<String> publisherTemp = new ArrayList<>();
            List<String> bookImageURLTemp = new ArrayList<>();

            try {
                // JDBC 드라이버 로드
                Class.forName("com.mysql.jdbc.Driver");
                // 데이터베이스에 연결 (url : "jdbc:mysql://10.0.2.2 (에뮬레이터 로컬 호스트 주소) :3306/your-database-name", user : DB 아이디, password : DB 비밀번호)
                conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

                // 쿼리 실행 (멤버 번호 알아내기)
                sql = "Select * from member_info where id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, id);
                rs = pstmt.executeQuery();

                // 결과 받기
                if (rs.next()) {
                    member_id = rs.getInt("member_id");
                }

                if(check == 0){
                    // 쿼리 실행 (멤버 번호를 통해 즐겨찾기 기록 알아내기)
                    sql = "Select * from favorite f join book b on f.book_id = b.book_id where f.member_id = ?";
                }
                else if(check == 1){
                    // 쿼리 실행 (멤버 번호를 통해 검색 기록 알아내기)
                    sql = "Select * from search_history sh join book b on sh.book_id = b.book_id where sh.member_id = ?";
                }

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, member_id);
                rs = pstmt.executeQuery();

                // 결과 받기
                while (rs.next()) {
                    booknameTemp.add(rs.getString("bookname"));
                    authorsTemp.add(rs.getString("authors"));
                    publisherTemp.add(rs.getString("publisher"));
                    bookImageURLTemp.add(rs.getString("book_image_URL"));
                }

                // 끝
                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            return new List[]{booknameTemp, authorsTemp, publisherTemp, bookImageURLTemp};
        }

        @Override
        protected void onPostExecute(List<String>[] result) {
            super.onPostExecute(result);
            // 어댑터에 데이터 변경 알리기
            if (adapter != null) {
                bookname.clear();
                authors.clear();
                publisher.clear();
                bookImageURL.clear();

                bookname.addAll(result[0]);
                authors.addAll(result[1]);
                publisher.addAll(result[2]);
                bookImageURL.addAll(result[3]);

                adapter.notifyDataSetChanged();
            }
        }
    }

}
