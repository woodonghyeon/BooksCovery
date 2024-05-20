package com.example.androidteamproject.ApiData;

import android.content.Context;

import com.example.androidteamproject.Search.LatelySearchBook;

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

    // LoanItems -> 대출 많은 도서를 뽑아옴 후에 수정 예정
    public void getLoanItems(String startDt, String endDt, int pageNo, int pageSize, String format, HttpResponseCallback<List<LatelySearchBook>> callback) {
        String url = BASE_URL + "loanItemSrch?authKey=" + API_KEY
                + "&startDt=" + startDt
                + "&endDt=" + endDt
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
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    JSONObject responseBody = new JSONObject(response.body().string());
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");
                    List<LatelySearchBook> books = new ArrayList<>();
                    for (int i = 0; i < docs.length(); i++) {
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");
                        String bookName = doc.getString("bookname"); // 예시로 책 이름 가져오기
                        String bookImageUrl = doc.getString("bookImageURL"); // 예시로 책 이미지 URL 가져오기
                        String authors = doc.getString("authors");
                        LatelySearchBook book = new LatelySearchBook(bookName, authors, bookImageUrl);
                        books.add(book);
                    }
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
        });
    }

}
