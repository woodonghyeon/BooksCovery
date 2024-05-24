package com.example.androidteamproject.ApiData;

import android.content.Context;

import com.example.androidteamproject.R;

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
        API_KEY = context.getString(R.string.api_key);
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

    public void bookSearchKeyword(String word, int pageNo, int pageSize, boolean tf, String format, final HttpResponseCallback callback) {
        String url = BASE_URL + "srchBooks?authKey=" + API_KEY + "&keyword=" + word + "&pageNo=" + pageNo + "&pageSize=" + pageSize + "&exactMatch=" + tf + "&format=" + format;
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
                    // 응답이 성공적이지 않은 경우 예외를 던짐
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // 응답 본문을 JSON 객체로 변환
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON 객체에서 "response" 객체를 가져와서 "docs" 배열을 추출
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBookKeyword 객체를 저장할 리스트 초기화
                    List<SearchBookKeyword> books = new ArrayList<>();

                    // docs 배열을 순회하며 각 문서에서 정보를 추출
                    for (int i = 0; i < docs.length(); i++) {
                        // 각 문서(doc) 객체를 가져옴
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // 책 이름(bookname)을 가져옴
                        String bookName = doc.getString("bookname");

                        // 책 이미지 URL(bookImageURL)을 가져옴
                        String bookImageUrl = doc.getString("bookImageURL");

                        // 저자(authors)를 가져옴
                        String authors = doc.getString("authors");

                        // 출판사를 가져옴
                        String publisher = doc.getString("publisher");

                        // 출판년도를 가져옴
                        String publication_year = doc.getString("publication_year");

                        // 책 정보를 담은 SearchBookKeyword 객체 생성
                        SearchBookKeyword book = new SearchBookKeyword(bookName, authors, bookImageUrl, publisher, publication_year);

                        // 생성한 SearchBookKeyword 객체를 리스트에 추가
                        books.add(book);
                    }

                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                }
            }
        });
    } // end of booksearchKeyword

    public void bookSearchTitle(String title, int pageNo, int pageSize, boolean tf, String format, final HttpResponseCallback callback) {
        String url = BASE_URL + "srchBooks?authKey=" + API_KEY + "&title=" + title + "&pageNo=" + pageNo + "&pageSize=" + pageSize + "&exactMatch=" + tf + "&format=" + format;
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
                    // 응답이 성공적이지 않은 경우 예외를 던짐
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // 응답 본문을 JSON 객체로 변환
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON 객체에서 "response" 객체를 가져와서 "docs" 배열을 추출
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBookKeyword 객체를 저장할 리스트 초기화
                    List<SearchBookKeyword> books = new ArrayList<>();

                    // docs 배열을 순회하며 각 문서에서 정보를 추출
                    for (int i = 0; i < docs.length(); i++) {
                        // 각 문서(doc) 객체를 가져옴
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // 책 이름(bookname)을 가져옴
                        String bookName = doc.getString("bookname");

                        // 책 이미지 URL(bookImageURL)을 가져옴
                        String bookImageUrl = doc.getString("bookImageURL");

                        // 저자(authors)를 가져옴
                        String authors = doc.getString("authors");

                        // 출판사를 가져옴
                        String publisher = doc.getString("publisher");

                        // 출판년도를 가져옴
                        String publication_year = doc.getString("publication_year");

                        // 책 정보를 담은 SearchBookKeyword 객체 생성
                        SearchBookKeyword book = new SearchBookKeyword(bookName, authors, bookImageUrl, publisher, publication_year);

                        // 생성한 SearchBookKeyword 객체를 리스트에 추가
                        books.add(book);
                    }

                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                }
            }
        });
    } // end of booksearchTitle

    // LoanItems -> 대출 많은 도서를 뽑아옴 후에 수정 예정
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
            // HTTP 응답을 처리하는 메서드
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // 응답이 성공적이지 않은 경우 예외를 던짐
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // 응답 본문을 JSON 객체로 변환
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON 객체에서 "response" 객체를 가져와서 "docs" 배열을 추출
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBook 객체를 저장할 리스트 초기화
                    List<SearchBook> books = new ArrayList<>();

                    // docs 배열을 순회하며 각 문서에서 정보를 추출
                    for (int i = 0; i < docs.length(); i++) {
                        // 각 문서(doc) 객체를 가져옴
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // 주제분류명(class_nm)을 가져옴
                        String class_nm = doc.getString("class_nm");

                        // 책 이름(bookname)을 가져옴
                        String bookName = doc.getString("bookname");

                        // 책 이미지 URL(bookImageURL)을 가져옴
                        String bookImageUrl = doc.getString("bookImageURL");

                        // 저자(authors)를 가져옴
                        String authors = doc.getString("authors");

                        // 책 정보를 담은 SearchBook 객체 생성
                        SearchBook book = new SearchBook(class_nm, bookName, authors, bookImageUrl);

                        // 생성한 SearchBook 객체를 리스트에 추가
                        books.add(book);
                    }

                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                }
            }
        });
    } // end of LoanItems

    // LoanItems -> 대출 많은 도서를 뽑아옴 후에 수정 예정
    public void getHotTrend(String searchDt, String format, HttpResponseCallback<List<SearchBook>> callback) {
        String url = BASE_URL + "loanItemSrch?authKey=" + API_KEY
                + "&searchDt=" + searchDt
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
            // HTTP 응답을 처리하는 메서드
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // 응답이 성공적이지 않은 경우 예외를 던짐
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // 응답 본문을 JSON 객체로 변환
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON 객체에서 "response" 객체를 가져와서 "docs" 배열을 추출
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBook 객체를 저장할 리스트 초기화
                    List<SearchBook> books = new ArrayList<>();

                    // docs 배열을 순회하며 각 문서에서 정보를 추출
                    for (int i = 0; i < docs.length(); i++) {
                        // 각 문서(doc) 객체를 가져옴
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // 주제분류명(class_nm)을 가져옴
                        String class_nm = doc.getString("class_nm");

                        // 책 이름(bookname)을 가져옴
                        String bookName = doc.getString("bookname");

                        // 책 이미지 URL(bookImageURL)을 가져옴
                        String bookImageUrl = doc.getString("bookImageURL");

                        // 저자(authors)를 가져옴
                        String authors = doc.getString("authors");

                        // 책 정보를 담은 SearchBook 객체 생성
                        SearchBook book = new SearchBook(class_nm, bookName, authors, bookImageUrl);

                        // 생성한 SearchBook 객체를 리스트에 추가
                        books.add(book);
                    }

                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                }
            }
        });
    } // end of hotTrend
}
