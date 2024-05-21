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
                    // ?ùë?ãµ?ù¥ ?Ñ±Í≥µÏ†Å?ù¥Ïß? ?ïä?? Í≤ΩÏö∞ ?òà?ô∏Î•? ?çòÏß?
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // ?ùë?ãµ Î≥∏Î¨∏?ùÑ JSON Í∞ùÏ≤¥Î°? Î≥??ôò
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON Í∞ùÏ≤¥?óê?Ñú "response" Í∞ùÏ≤¥Î•? Í∞??†∏???Ñú "docs" Î∞∞Ïó¥?ùÑ Ï∂îÏ∂ú
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBookKeyword Í∞ùÏ≤¥Î•? ???û•?ï† Î¶¨Ïä§?ä∏ Ï¥àÍ∏∞?ôî
                    List<SearchBookKeyword> books = new ArrayList<>();

                    // docs Î∞∞Ïó¥?ùÑ ?àú?öå?ïòÎ©? Í∞? Î¨∏ÏÑú?óê?Ñú ?†ïÎ≥¥Î?? Ï∂îÏ∂ú
                    for (int i = 0; i < docs.length(); i++) {
                        // Í∞? Î¨∏ÏÑú(doc) Í∞ùÏ≤¥Î•? Í∞??†∏?ò¥
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // Ï±? ?ù¥Î¶?(bookname)?ùÑ Í∞??†∏?ò¥
                        String bookName = doc.getString("bookname");

                        // Ï±? ?ù¥ÎØ∏Ï? URL(bookImageURL)?ùÑ Í∞??†∏?ò¥
                        String bookImageUrl = doc.getString("bookImageURL");

                        // ???ûê(authors)Î•? Í∞??†∏?ò¥
                        String authors = doc.getString("authors");

                        // Ï∂úÌåê?Ç¨Î•? Í∞??†∏?ò¥
                        String publisher = doc.getString("publisher");

                        // Ï∂úÌåê?ÖÑ?èÑÎ•? Í∞??†∏?ò¥
                        String publication_year = doc.getString("publication_year");

                        // Ï±? ?†ïÎ≥¥Î?? ?ã¥?? SearchBookKeyword Í∞ùÏ≤¥ ?Éù?Ñ±
                        SearchBookKeyword book = new SearchBookKeyword(bookName, authors, bookImageUrl, publisher, publication_year);

                        // ?Éù?Ñ±?ïú SearchBookKeyword Í∞ùÏ≤¥Î•? Î¶¨Ïä§?ä∏?óê Ï∂îÍ?
                        books.add(book);
                    }

                    // ÏΩúÎ∞±?ùÑ ?Üµ?ï¥ ?Ñ±Í≥µÏ†Å?ù∏ ?ùë?ãµ Ï≤òÎ¶¨ (Ï±? Î¶¨Ïä§?ä∏ ?†Ñ?ã¨)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON ?åå?ã± Ï§? ?òà?ô∏Í∞? Î∞úÏÉù?ïú Í≤ΩÏö∞ ÏΩúÎ∞±?ùÑ ?Üµ?ï¥ ?ã§?å® Ï≤òÎ¶¨
                    callback.onFailure(e);
                }
            }
        });
    } // end of booksearch

    // LoanItems -> ??Ï∂? ÎßéÏ? ?èÑ?ÑúÎ•? ÎΩëÏïÑ?ò¥ ?õÑ?óê ?àò?†ï ?òà?†ï
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
            // HTTP ?ùë?ãµ?ùÑ Ï≤òÎ¶¨?ïò?äî Î©îÏÑú?ìú
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // ?ùë?ãµ?ù¥ ?Ñ±Í≥µÏ†Å?ù¥Ïß? ?ïä?? Í≤ΩÏö∞ ?òà?ô∏Î•? ?çòÏß?
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // ?ùë?ãµ Î≥∏Î¨∏?ùÑ JSON Í∞ùÏ≤¥Î°? Î≥??ôò
                    JSONObject responseBody = new JSONObject(response.body().string());

                    // JSON Í∞ùÏ≤¥?óê?Ñú "response" Í∞ùÏ≤¥Î•? Í∞??†∏???Ñú "docs" Î∞∞Ïó¥?ùÑ Ï∂îÏ∂ú
                    JSONArray docs = responseBody.getJSONObject("response").getJSONArray("docs");

                    // SearchBook Í∞ùÏ≤¥Î•? ???û•?ï† Î¶¨Ïä§?ä∏ Ï¥àÍ∏∞?ôî
                    List<SearchBook> books = new ArrayList<>();

                    // docs Î∞∞Ïó¥?ùÑ ?àú?öå?ïòÎ©? Í∞? Î¨∏ÏÑú?óê?Ñú ?†ïÎ≥¥Î?? Ï∂îÏ∂ú
                    for (int i = 0; i < docs.length(); i++) {
                        // Í∞? Î¨∏ÏÑú(doc) Í∞ùÏ≤¥Î•? Í∞??†∏?ò¥
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");

                        // Ï£ºÏ†úÎ∂ÑÎ•òÎ™?(class_nm)?ùÑ Í∞??†∏?ò¥
                        String class_nm = doc.getString("class_nm");

                        // Ï±? ?ù¥Î¶?(bookname)?ùÑ Í∞??†∏?ò¥
                        String bookName = doc.getString("bookname");

                        // Ï±? ?ù¥ÎØ∏Ï? URL(bookImageURL)?ùÑ Í∞??†∏?ò¥
                        String bookImageUrl = doc.getString("bookImageURL");

                        // ???ûê(authors)Î•? Í∞??†∏?ò¥
                        String authors = doc.getString("authors");

                        // Ï±? ?†ïÎ≥¥Î?? ?ã¥?? SearchBook Í∞ùÏ≤¥ ?Éù?Ñ±
                        SearchBook book = new SearchBook(class_nm, bookName, authors, bookImageUrl);

                        // ?Éù?Ñ±?ïú SearchBook Í∞ùÏ≤¥Î•? Î¶¨Ïä§?ä∏?óê Ï∂îÍ?
                        books.add(book);
                    }

                    // ÏΩúÎ∞±?ùÑ ?Üµ?ï¥ ?Ñ±Í≥µÏ†Å?ù∏ ?ùë?ãµ Ï≤òÎ¶¨ (Ï±? Î¶¨Ïä§?ä∏ ?†Ñ?ã¨)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON ?åå?ã± Ï§? ?òà?ô∏Í∞? Î∞úÏÉù?ïú Í≤ΩÏö∞ ÏΩúÎ∞±?ùÑ ?Üµ?ï¥ ?ã§?å® Ï≤òÎ¶¨
                    callback.onFailure(e);
                }
            }
        });
    }

}
