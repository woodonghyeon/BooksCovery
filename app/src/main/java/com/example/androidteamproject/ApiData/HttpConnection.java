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
                    // ??΅?΄ ?±κ³΅μ ?΄μ§? ??? κ²½μ° ??Έλ₯? ?μ§?
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // ??΅ λ³Έλ¬Έ? JSON κ°μ²΄λ‘? λ³??
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON κ°μ²΄?? "response" κ°μ²΄λ₯? κ°?? Έ??? "docs" λ°°μ΄? μΆμΆ
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBookKeyword κ°μ²΄λ₯? ???₯?  λ¦¬μ€?Έ μ΄κΈ°?
                    List<SearchBookKeyword> books = new ArrayList<>();

                    // docs λ°°μ΄? ???λ©? κ°? λ¬Έμ?? ? λ³΄λ?? μΆμΆ
                    for (int i = 0; i < docs.length(); i++) {
                        // κ°? λ¬Έμ(doc) κ°μ²΄λ₯? κ°?? Έ?΄
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // μ±? ?΄λ¦?(bookname)? κ°?? Έ?΄
                        String bookName = doc.getString("bookname");

                        // μ±? ?΄λ―Έμ? URL(bookImageURL)? κ°?? Έ?΄
                        String bookImageUrl = doc.getString("bookImageURL");

                        // ???(authors)λ₯? κ°?? Έ?΄
                        String authors = doc.getString("authors");

                        // μΆν?¬λ₯? κ°?? Έ?΄
                        String publisher = doc.getString("publisher");

                        // μΆν??λ₯? κ°?? Έ?΄
                        String publication_year = doc.getString("publication_year");

                        // μ±? ? λ³΄λ?? ?΄?? SearchBookKeyword κ°μ²΄ ??±
                        SearchBookKeyword book = new SearchBookKeyword(bookName, authors, bookImageUrl, publisher, publication_year);

                        // ??±? SearchBookKeyword κ°μ²΄λ₯? λ¦¬μ€?Έ? μΆκ?
                        books.add(book);
                    }

                    // μ½λ°±? ?΅?΄ ?±κ³΅μ ?Έ ??΅ μ²λ¦¬ (μ±? λ¦¬μ€?Έ ? ?¬)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON ??± μ€? ??Έκ°? λ°μ? κ²½μ° μ½λ°±? ?΅?΄ ?€?¨ μ²λ¦¬
                    callback.onFailure(e);
                }
            }
        });
    } // end of booksearch

    // LoanItems -> ??μΆ? λ§μ? ??λ₯? λ½μ?΄ ?? ??  ?? 
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
            // HTTP ??΅? μ²λ¦¬?? λ©μ?
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // ??΅?΄ ?±κ³΅μ ?΄μ§? ??? κ²½μ° ??Έλ₯? ?μ§?
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // ??΅ λ³Έλ¬Έ? JSON κ°μ²΄λ‘? λ³??
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON κ°μ²΄?? "response" κ°μ²΄λ₯? κ°?? Έ??? "docs" λ°°μ΄? μΆμΆ
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBook κ°μ²΄λ₯? ???₯?  λ¦¬μ€?Έ μ΄κΈ°?
                    List<SearchBook> books = new ArrayList<>();

                    // docs λ°°μ΄? ???λ©? κ°? λ¬Έμ?? ? λ³΄λ?? μΆμΆ
                    for (int i = 0; i < docs.length(); i++) {
                        // κ°? λ¬Έμ(doc) κ°μ²΄λ₯? κ°?? Έ?΄
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // μ£Όμ λΆλ₯λͺ?(class_nm)? κ°?? Έ?΄
                        String class_nm = doc.getString("class_nm");

                        // μ±? ?΄λ¦?(bookname)? κ°?? Έ?΄
                        String bookName = doc.getString("bookname");

                        // μ±? ?΄λ―Έμ? URL(bookImageURL)? κ°?? Έ?΄
                        String bookImageUrl = doc.getString("bookImageURL");

                        // ???(authors)λ₯? κ°?? Έ?΄
                        String authors = doc.getString("authors");

                        // μ±? ? λ³΄λ?? ?΄?? SearchBook κ°μ²΄ ??±
                        SearchBook book = new SearchBook(class_nm, bookName, authors, bookImageUrl);

                        // ??±? SearchBook κ°μ²΄λ₯? λ¦¬μ€?Έ? μΆκ?
                        books.add(book);
                    }

                    // μ½λ°±? ?΅?΄ ?±κ³΅μ ?Έ ??΅ μ²λ¦¬ (μ±? λ¦¬μ€?Έ ? ?¬)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON ??± μ€? ??Έκ°? λ°μ? κ²½μ° μ½λ°±? ?΅?΄ ?€?¨ μ²λ¦¬
                    callback.onFailure(e);
                }
            }
        });
    }

}
