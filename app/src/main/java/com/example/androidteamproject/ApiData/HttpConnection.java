package com.example.androidteamproject.ApiData;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpConnection {

    private static final String BASE_URL = "http://data4library.kr/api/";
    private static String API_KEY;
    private static HttpConnection instance;
    private OkHttpClient client;

    private HttpConnection(Context context) {
        client = new OkHttpClient();
        API_KEY = "***REMOVED***";
    }

    public static HttpConnection getInstance(Context context) {
        if (instance == null) {
            instance = new HttpConnection(context);
        }
        return instance;
    }

    public interface HttpResponseCallback<T> {
        void onSuccess(T responseData);
        void onFailure(Exception e);
    }

    public void getKeyword(String format, final HttpResponseCallback callback) {
        String url = BASE_URL + "monthlyKeywords?authKey=" + API_KEY + "&month=2024-04" + "&format=" + format;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    callback.onSuccess(responseData);
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public void bookSearch(String word, int pageNo, int pageSize, String format, final HttpResponseCallback callback) {
        String url = BASE_URL + "srchBooks?authKey=" + API_KEY + "&keyword=" + word + "&pageNo=" + pageNo + "&pageSize=" + pageSize + "&format=" + format;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // ?��?��?�� ?��공적?���? ?��?? 경우 ?��?���? ?���?
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // ?��?�� 본문?�� JSON 객체�? �??��
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON 객체?��?�� "response" 객체�? �??��???�� "docs" 배열?�� 추출
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBookKeyword 객체�? ???��?�� 리스?�� 초기?��
                    List<SearchBookKeyword> books = new ArrayList<>();

                    // docs 배열?�� ?��?��?���? �? 문서?��?�� ?��보�?? 추출
                    for (int i = 0; i < docs.length(); i++) {
                        // �? 문서(doc) 객체�? �??��?��
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // �? ?���?(bookname)?�� �??��?��
                        String bookName = doc.getString("bookname");

                        // �? ?��미�? URL(bookImageURL)?�� �??��?��
                        String bookImageUrl = doc.getString("bookImageURL");

                        // ???��(authors)�? �??��?��
                        String authors = doc.getString("authors");

                        // 출판?���? �??��?��
                        String publisher = doc.getString("publisher");

                        // 출판?��?���? �??��?��
                        String publication_year = doc.getString("publication_year");

                        // �? ?��보�?? ?��?? SearchBookKeyword 객체 ?��?��
                        SearchBookKeyword book = new SearchBookKeyword(bookName, authors, bookImageUrl, publisher, publication_year);

                        // ?��?��?�� SearchBookKeyword 객체�? 리스?��?�� 추�?
                        books.add(book);
                    }

                    // 콜백?�� ?��?�� ?��공적?�� ?��?�� 처리 (�? 리스?�� ?��?��)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON ?��?�� �? ?��?���? 발생?�� 경우 콜백?�� ?��?�� ?��?�� 처리
                    callback.onFailure(e);
                }
            }
        });
    } // end of booksearch

    // LoanItems -> ??�? 많�? ?��?���? 뽑아?�� ?��?�� ?��?�� ?��?��
    public void getLoanItems(String startDt, String endDt, String from_age, String to_age, int pageNo, int pageSize, String format, HttpResponseCallback<List<SearchBook>> callback) {
        String url = BASE_URL + "loanItemSrch?authKey=" + API_KEY
                + "&startDt=" + startDt
                + "&endDt=" + endDt
                + "&from_age=" + from_age
                + "&to_age=" + to_age
                + "&pageNo=" + pageNo
                + "&pageSize=" + pageSize
                + "&format=" + format;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            // HTTP ?��?��?�� 처리?��?�� 메서?��
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // ?��?��?�� ?��공적?���? ?��?? 경우 ?��?���? ?���?
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // ?��?�� 본문?�� JSON 객체�? �??��
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON 객체?��?�� "response" 객체�? �??��???�� "docs" 배열?�� 추출
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBook 객체�? ???��?�� 리스?�� 초기?��
                    List<SearchBook> books = new ArrayList<>();

                    // docs 배열?�� ?��?��?���? �? 문서?��?�� ?��보�?? 추출
                    for (int i = 0; i < docs.length(); i++) {
                        // �? 문서(doc) 객체�? �??��?��
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // 주제분류�?(class_nm)?�� �??��?��
                        String class_nm = doc.getString("class_nm");

                        // �? ?���?(bookname)?�� �??��?��
                        String bookName = doc.getString("bookname");

                        // �? ?��미�? URL(bookImageURL)?�� �??��?��
                        String bookImageUrl = doc.getString("bookImageURL");

                        // ???��(authors)�? �??��?��
                        String authors = doc.getString("authors");

                        // �? ?��보�?? ?��?? SearchBook 객체 ?��?��
                        SearchBook book = new SearchBook(class_nm, bookName, authors, bookImageUrl);

                        // ?��?��?�� SearchBook 객체�? 리스?��?�� 추�?
                        books.add(book);
                    }

                    // 콜백?�� ?��?�� ?��공적?�� ?��?�� 처리 (�? 리스?�� ?��?��)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON ?��?�� �? ?��?���? 발생?�� 경우 콜백?�� ?��?�� ?��?�� 처리
                    callback.onFailure(e);
                }
            }
        });
    }

}
