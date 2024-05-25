package com.example.androidteamproject.Home;

import android.content.Context;
import android.util.Log;

import com.example.androidteamproject.ApiData.SearchBookKeyword;
import com.example.androidteamproject.SessionManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.transform.Result;

public class DepartmentCount {

    public String insertBookCount(Integer department_id, Integer book_id){
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.jdbc.Driver");
            // 데이터베이스에 연결 (url : "jdbc:mysql://10.0.2.2 (에뮬레이터 로컬 호스트 주소) :3306/your-database-name", user : DB 아이디, password : DB 비밀번호)
            conn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/test", "root", "root");

            // 쿼리 실행
            String sql = "Insert into book_count (department_id, book_id) Values (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, department_id);
            pstmt.setInt(2, book_id);

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

    public String updateBookCount(Integer book_count_id){
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




    public void selectBookCount(Integer department_id) throws SQLException {
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
            String sql = "select b.bookname, b.authors, b.publisher, b.book_image_URL, b.publication_year, b.class_no, b.loan_count, c.book_count" +
                    " from book b, book_count c" +
                    " where b.book_id = c.book_id and department_id = ?" +
                    " order by c.book_count desc" +
                    " limit 10";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, department_id);

            //pstmt.setString(10, currentTime);  //지금 사용 X (회원가입 한 시간)

            // 쿼리 실행
            rs = pstmt.executeQuery(sql);


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

            String class_no = rs.getString("class_no");

            int loan_count = rs.getInt("loan_count");

            int book_count = rs.getInt("book_count");

            // 책 정보를 담은 SearchBookKeyword 객체 생성
            SearchBookKeyword book = new SearchBookKeyword(bookName, authors, bookImageUrl, publisher, publication_year);   // 수정필요

            // 생성한 SearchBookKeyword 객체를 리스트에 추가
            books.add(book);    // 수정필요
        }
    }

}
