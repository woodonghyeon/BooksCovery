package com.example.androidteamproject.ApiData;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataBase {
    private static final String BASE_URL = "***REMOVED***"; // 서버 URL
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;
    private Gson gson;

    // DB연결 요청
    public DataBase() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    // POST
    private void postRequest(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // PUT
    private void putRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create("", JSON))
                .build();

        client.newCall(request).enqueue(callback);
    }

    // GET
    private void getRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    // DELETE
    private void deleteRequest(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // Book Insert
    public void insertBook(SearchBookDetail bookDetail, Callback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("bookName", bookDetail.getBookName());
        data.put("isbn13", bookDetail.getIsbn13());
        data.put("authors", bookDetail.getAuthors());
        data.put("publisher", bookDetail.getPublisher());
        data.put("bookImageUrl", bookDetail.getBookImageUrl());
        data.put("publication_year", bookDetail.getPublication_year());
        data.put("class_no", bookDetail.getClass_no());
        data.put("loanCnt", bookDetail.getLoanCnt());

        String json = gson.toJson(data);
        postRequest(BASE_URL + "/books/add", json, callback);
    }

    // BookId Select
    public void selectBookId(String isbn13, Callback callback) {
        getRequest(BASE_URL + "/books/" + isbn13, callback);
    }

    // History Insert
    public void insertHistory(int member_id, int book_id, Callback callback) {
        Map<String, Integer> data = new HashMap<>();
        data.put("member_id", member_id);
        data.put("book_id", book_id);

        String json = gson.toJson(data);
        postRequest(BASE_URL + "/history/add", json, callback);
    }

    // BookCount Find
    public void findBookCount(int book_id, int department_id, Callback callback) {
        getRequest(BASE_URL + "/book_count/" + book_id + "/" + department_id, callback);
    }

    // BookCount Update
    public void updateBookCount(int book_count_id, Callback callback) {
        putRequest(BASE_URL + "/book_count/update/" + book_count_id, callback);
    }

    // BookCount Insert
    public void insertBookCount(int department_id, int book_id, Callback callback) {
        Map<String, Integer> data = new HashMap<>();
        data.put("department_id", department_id);
        data.put("book_id", book_id);

        String json = gson.toJson(data);
        postRequest(BASE_URL + "/book_count/add", json, callback);
    }

    // 즐겨찾기 확인
    public void isFavorite(Integer member_id, Integer book_id, Callback callback) {
        getRequest(BASE_URL + "/favorite?member_id=" + member_id + "&book_id=" + book_id, callback);
    }

    // 즐겨찾기 추가
    public void addFavorite(int member_id, int book_id, Callback callback) {
        Map<String, Integer> data = new HashMap<>();
        data.put("member_id", member_id);
        data.put("book_id", book_id);

        String json = gson.toJson(data);
        postRequest(BASE_URL + "/favorite/" + book_id, json, callback);
    }

    // 즐겨찾기 삭제
    public void removeFavorite(int member_id, int book_id, Callback callback) {
        String json = gson.toJson(new HashMap<>());
        deleteRequest(BASE_URL + "/favorite/" + book_id, json, callback);
    }

    // 학과별 인기도서 데이터 요청
    public void getPopularBooks(int departmentId, Callback callback) {
        String url = BASE_URL + "/api/popular?departmentId=" + departmentId;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    // 학과별 인기도서 데이터 응답/파싱
    public List<SearchBookDetail> parsePopularBooksResponse(Response response) throws IOException {
        String jsonResponse = response.body().string();
        List<SearchBookDetail> bookList = new ArrayList<>();

        try {
            JSONArray responseArray = new JSONArray(jsonResponse);
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject bookObject = responseArray.getJSONObject(i);

                SearchBookDetail bookDetail = new SearchBookDetail();
                bookDetail.setBookName(bookObject.optString("bookname", "N/A"));
                bookDetail.setAuthors(bookObject.optString("authors", "N/A"));
                bookDetail.setPublisher(bookObject.optString("publisher", "N/A"));
                bookDetail.setPublication_year(bookObject.optInt("publication_year", 0));
                bookDetail.setClass_no(bookObject.optString("class_no", "N/A"));
                bookDetail.setLoanCnt(bookObject.optInt("loan_count", 0));
                bookDetail.setBookImageUrl(bookObject.optString("book_image_URL", ""));
                bookDetail.setIsbn13(bookObject.optString("isbn", "N/A"));

                bookList.add(bookDetail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bookList;
    } // end of parsePopularBooksResponse


    // 회원가입
    public void addMember(String name, String gender, String age, String department_id, String id, String password, String email, Callback callback) {
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("gender", gender);
        data.put("age", age);
        data.put("department_id", department_id);
        data.put("id", id);
        data.put("password", password);
        data.put("email", email);

        String json = gson.toJson(data);
        postRequest(BASE_URL + "/join/add", json, callback);
    }

    // 로그인
    public void login(String id, String password, Callback callback) {
        String url = BASE_URL + "/login";
        RequestBody formBody = new FormBody.Builder()
                .add("id", id)
                .add("password", password)
                .add("toURL", "")
                .add("rememberId", "false")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 로그인 아이디 중복체크
    public void checkIdDuplicate(String id, Callback callback) { 
        String url = BASE_URL + "/join/check_id";
        RequestBody formBody = new FormBody.Builder()
                .add("id", id)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
