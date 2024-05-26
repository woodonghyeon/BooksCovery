package com.example.androidteamproject.ApiData;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    // 도서 중복 확인 중복시 안하고 없을시 추가
    // 검색 기록 도서pk확인 있으면 삭제하고 추가 없으면 추가
    //// 즐겨찾기 온클릭시 즐겨찾기 추가
    // 학과별 검색횟수 있으면 업데이트 없으면 추가
    Connection conn = null;
    PreparedStatement pstmt = null;
    public void dbConn() throws ClassNotFoundException, SQLException {
        // JDBC 드라이버 로드
        Class.forName("com.mysql.jdbc.Driver");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // 데이터베이스에 연결 (url : "jdbc:mysql://10.0.2.2 (에뮬레이터 로컬 호스트 주소) :3306/your-database-name", user : DB 아이디, password : DB 비밀번호)
        conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");
    }

    public int selectBookId(SearchBookDetail sbd){
        ResultSet rs = null;
        try {
            dbConn();
            String sql = "select book_id from book where isbn = ?";
            // 쿼리 실행
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sbd.getIsbn13());  //String 저장 어떻게 할건지 수정필요

            // 쿼리 실행
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("book_id");
            }

            return 1;
        } catch (Exception e) {
            Log.e("InsertDataTask", "Error inserting data", e);
//            return "데이터 삽입 중 오류 발생";
            return 0; // 이거 어떻게 리턴할 지 ,..?

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                Log.e("InsertDataTask", "Error closing connection", e);
            }
        }
    }

    public int checkDuplicate(SearchBookDetail sbd, String sql) { //상세보기에 들어가면 체크
        ResultSet rs = null;
        try {
            dbConn();
            // 쿼리 실행
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sbd.getIsbn13());  //String 저장 어떻게 할건지 수정필요

            // 쿼리 실행
            rs = pstmt.executeQuery();
            if (!rs.next()) {
                return 0;
            }

            return 1;
        } catch (Exception e) {
            Log.e("InsertDataTask", "Error inserting data", e);
//            return "데이터 삽입 중 오류 발생";
            return 2; // 이거 어떻게 리턴할 지 ,..?

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                Log.e("InsertDataTask", "Error closing connection", e);
            }
        }
    }

    public String insertBook(SearchBookDetail sbd){
        String sql1 = "select isbn from book where isbn = ?";
        if (checkDuplicate(sbd, sql1) == 0) {
            try {
                dbConn();

                // 쿼리 실행
                String sql = "Insert into book (bookname, isbn, authors, publisher, book_image_URL, publication_year, class_no, loan_count) Values (?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, sbd.getBookName());
                pstmt.setString(2, sbd.getIsbn13()); //String 저장 어떻게 할건지 수정필요
                pstmt.setString(3, sbd.getAuthors());
                pstmt.setString(4, sbd.getPublisher());
                pstmt.setString(5, sbd.getBookImageUrl());
                pstmt.setString(6, sbd.getPublication_year()); //String 저장 어떻게 할건지 수정필요
                pstmt.setString(7, sbd.getClass_no()); //String 저장 어떻게 할건지 수정필요
                pstmt.setString(8, sbd.getLoanCnt()); //String 저장 어떻게 할건지 수정필요

                //pstmt.setString(10, currentTime);  //지금 사용 X (회원가입 한 시간)

                // 쿼리 실행
                int rowsInserted = pstmt.executeUpdate();
                return rowsInserted > 0 ? "데이터 삽입 성공" : "데이터 삽입 실패";

            } catch (Exception e) {
                Log.e("InsertDataTask", "Error inserting data", e);
                return "데이터 삽입 중 오류 발생";

            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (Exception e) {
                    Log.e("InsertDataTask", "Error closing connection", e);
                }
            }
        }
        return "중복 있음.";
    }

    public String insertBookCount(Integer department_id, Integer book_id){ //학과별 도서 검색횟수
        try {
            dbConn();

            // 쿼리 실행
            String sql = "Insert into book_count (department_id, book_id) Values (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, department_id);
            pstmt.setInt(2, book_id);

            // 쿼리 실행
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0 ? "데이터 삽입 성공" : "데이터 삽입 실패";

        } catch (Exception e) {
            Log.e("InsertDataTask", "Error inserting data", e);
            return "데이터 삽입 중 오류 발생";

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                Log.e("InsertDataTask", "Error closing connection", e);
            }
        }
    }

    public int findBookCount(int book_id, int department_id){
        ResultSet rs = null;
        try {
            dbConn();
            String sql = "select book_count_id from book_count where book_id = ? and department_id = ?";
            // 쿼리 실행
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, book_id);  //String 저장 어떻게 할건지 수정필요
            pstmt.setInt(2, department_id);
            // 쿼리 실행
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("book_count_id");
            }

            return 1;
        } catch (Exception e) {
            Log.e("InsertDataTask", "Error inserting data", e);
//            return "데이터 삽입 중 오류 발생";
            return 0; // 이거 어떻게 리턴할 지 ,..?

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                Log.e("InsertDataTask", "Error closing connection", e);
            }
        }
    }

    public String updateBookCount(Integer book_count_id){ //학과별 도서검색 카운트 증가
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.jdbc.Driver");
            // 데이터베이스에 연결 (url : "jdbc:mysql://10.0.2.2 (에뮬레이터 로컬 호스트 주소) :3306/your-database-name", user : DB 아이디, password : DB 비밀번호)
            conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

            // 쿼리 실행
            String sql = "update book_count set book_count = book_count + 1 where book_count_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, book_count_id);

            // 쿼리 실행
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0 ? "데이터 수정 성공" : "데이터 수정 실패";

        } catch (Exception e) {
            Log.e("UpdateDataTask", "Error updating data", e);
            return "데이터 수정 중 오류 발생";

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                Log.e("UpdateDataTask", "Error closing connection", e);
            }
        }
    }

    public List<SearchBookKeyword> selectBookCount(Integer department_id) throws SQLException { //학과별 도서검색 탑10
        //기간 안지났으면 가져오고
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SearchBookKeyword> books = new ArrayList<>();
        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.jdbc.Driver");
            // 데이터베이스에 연결 (url : "jdbc:mysql://10.0.2.2 (에뮬레이터 로컬 호스트 주소) :3306/your-database-name", user : DB 아이디, password : DB 비밀번호)
            conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

            // 쿼리 실행
            String sql = "select b.isbn, b.bookname, b.authors, b.publisher, b.book_image_URL, b.publication_year, b.class_no, b.loan_count, c.book_count" +
                    " from book b, book_count c" +
                    " where b.book_id = c.book_id and department_id = ?" +
                    " order by c.book_count desc" +
                    " limit 10";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, department_id);

            // 쿼리 실행
            rs = pstmt.executeQuery();


//            return rowsInserted > 0 ? "데이터 삽입 성공" : "데이터 삽입 실패";

        } catch (Exception e) {
            Log.e("InsertDataTask", "Error inserting data", e);
//            return "데이터 삽입 중 오류 발생";

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                Log.e("InsertDataTask", "Error closing connection", e);
            }
        }

        while(rs.next()){
            // 책 이름(bookname)을 가져옴
            String bookName = rs.getString("bookname");

            // 책 이미지 URL(bookImageURL)을 가져옴
            String bookImageUrl = rs.getString("book_image_URL");

            // 저자(authors)를 가져옴
            String authors = rs.getString("authors");

            // 출판사를 가져옴
            String publisher = rs.getString("publisher");

            // 출판년도를 가져옴
            String publication_year = rs.getString("publication_year");

            String isbn13 = rs.getString("isbn");

            String class_no = rs.getString("class_no");

            int loan_count = rs.getInt("loan_count");

            int book_count = rs.getInt("book_count");

            // 책 정보를 담은 SearchBookKeyword 객체 생성
            SearchBookKeyword book = new SearchBookKeyword(isbn13, bookName, authors, bookImageUrl, publisher, publication_year);   // 수정필요

            // 생성한 SearchBookKeyword 객체를 리스트에 추가
            books.add(book);    // 수정필요
        }
        return books;
    }

    // 검색 기록 DB 삽입
    public String insertHistory(int member_id, int book_id) {
        try {
            dbConn();

            String sql = "insert into search_history (member_id, book_id, search_date) values(?, ?, now()) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, member_id);
            pstmt.setInt(2, book_id);

            // 쿼리 실행
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0 ? "데이터 삽입 성공" : "데이터 삽입 실패";

        } catch (Exception e) {
            Log.e("History Insert Error" , e.toString());
            return e.toString();
        } finally {
            // DB 연결 끊기
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                Log.e("InsertDataTask", "Error closing connection", e);
            }
        }
    }
}
