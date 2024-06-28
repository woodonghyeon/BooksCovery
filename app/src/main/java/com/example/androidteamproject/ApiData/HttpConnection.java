package com.example.androidteamproject.ApiData;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.androidteamproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpConnection {

    private static final String BASE_URL = "http://10.0.2.2/api/";
    private static HttpConnection instance;
    private OkHttpClient client;

    private HttpConnection(Context context) {
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();
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

    public void getKeyword(final HttpResponseCallback callback) {
        String url = BASE_URL + "keyword";
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

    public void bookSearchKeyword(String word, int pageNo, int pageSize, boolean tf, final HttpResponseCallback callback) {
        String url = BASE_URL + "search?keyword=" + word + "&pageNo=" + pageNo + "&pageSize=" + pageSize + "&exactMatch=" + tf;
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
                        String isbn13 = doc.getString("isbn13");
                        // 책 정보를 담은 SearchBookKeyword 객체 생성
                        SearchBookKeyword book = new SearchBookKeyword(isbn13, bookName, authors, bookImageUrl, publisher, publication_year);
                        // 생성한 SearchBookKeyword 객체를 리스트에 추가
                        books.add(book);
                    }
                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                } finally {
                    response.close();
                }
            }
        });
    } // end of booksearchKeyword

    public void bookSearchAuthor(String author, int pageNo, int pageSize, boolean tf, final HttpResponseCallback callback) {
        String url = BASE_URL + "search?author=" + author + "&pageNo=" + pageNo + "&pageSize=" + pageSize + "&exactMatch=" + tf;
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

                    // SearchBookTitle 객체를 저장할 리스트 초기화
                    List<SearchBookAuthor> books = new ArrayList<>();
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
                        String isbn13 = doc.getString("isbn13");
                        // 책 정보를 담은 SearchBookKeyword 객체 생성
                        SearchBookAuthor book = new SearchBookAuthor(isbn13, bookName, authors, bookImageUrl, publisher, publication_year);
                        // 생성한 SearchBookKeyword 객체를 리스트에 추가
                        books.add(book);
                    }
                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                } finally {
                    response.close();
                }
            }
        });
    } // end of booksearchAuthor

    public void bookSearchTitle(String title, int pageNo, int pageSize, boolean tf, final HttpResponseCallback callback) {
        String url = BASE_URL + "search?title=" + title + "&pageNo=" + pageNo + "&pageSize=" + pageSize + "&exactMatch=" + tf;
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

                    // SearchBookTitle 객체를 저장할 리스트 초기화
                    List<SearchBookTitle> books = new ArrayList<>();
                    // docs 배열을 순회하며 각 문서에서 정보를 추출
                    for (int i = 0; i < docs.length(); i++) {
                        // 각 문서(doc) 객체를 가져옴
                        JSONObject doc = docs.getJSONObject(i).getJSONObject("doc");
                        // isbn13자리를 가져옴
                        String isbn = doc.getString("isbn13");
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
                        // 책 정보를 담은 SearchBookTitle 객체 생성
                        SearchBookTitle book = new SearchBookTitle(isbn ,bookName, authors, bookImageUrl, publisher, publication_year);
                        // 생성한 SearchBookTitle 객체를 리스트에 추가
                        books.add(book);
                    }
                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                } finally {
                    response.close();
                }
            }
        });
    } // end of booksearchTitle

    // LoanItems -> 대출 많은 도서를 뽑아옴
    public void getLoanItems(String startDt, String endDt, String from_age, String to_age, int pageNo, int pageSize, HttpResponseCallback<List<SearchBook>> callback) {
        String url = BASE_URL + "popular"
                + "?startDt=" + startDt
                + "&endDt=" + endDt
                + "&from_age=" + from_age
                + "&to_age=" + to_age
                + "&pageNo=" + pageNo
                + "&pageSize=" + pageSize;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // 응답이 성공적이지 않은 경우 예외를 던짐
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    // 응답 본문을 JSON 객체로 변환
                    JSONArray docs = new JSONArray(response.body().string());
                    // SearchBook 객체를 저장할 리스트 초기화
                    List<SearchBook> books = new ArrayList<>();
                    // docs 배열을 순회하며 각 문서에서 정보를 추출
                    for (int i = 0; i < docs.length(); i++) {
                        // 각 문서(doc) 객체를 가져옴
                        JSONObject doc = docs.getJSONObject(i);
                        // 주제분류명(class_nm)을 가져옴
                        String class_nm = doc.getString("class_nm");
                        // 책 이름(bookname)을 가져옴
                        String bookName = doc.getString("bookname");
                        // 책 이미지 URL(bookImageURL)을 가져옴
                        String bookImageUrl = doc.getString("bookImageURL");
                        // 저자(authors)를 가져옴
                        String authors = doc.getString("authors");
                        String isbn13 = doc.getString("isbn13");
                        // 책 정보를 담은 SearchBook 객체 생성
                        SearchBook book = new SearchBook(class_nm, bookName, authors, bookImageUrl, isbn13);
                        // 생성한 SearchBook 객체를 리스트에 추가
                        books.add(book);
                    }
                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                } finally {
                    response.close();
                }
            }
        });
    } // end of LoanItems

    // LoanItems -> 대출 많은 도서를 뽑아옴 후에 수정 예정
    public void getHotTrend(String searchDt, HttpResponseCallback<List<SearchBook>> callback) {
        String url = BASE_URL + "increase?searchDt=" + searchDt;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

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
                    // JSON 객체에서 "response" 객체를 가져옴
                    JSONObject responseObject = responseBody.getJSONObject("response");
                    JSONArray results = responseObject.getJSONArray("results");

                    // SearchBook 객체를 저장할 리스트 초기화
                    List<SearchBook> books = new ArrayList<>();
                    // results 배열을 순회하며 각 결과에서 docs 배열을 추출
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject resultObject = results.getJSONObject(i).getJSONObject("result");
                        JSONArray docs = resultObject.getJSONArray("docs");

                        // docs 배열을 순회하며 각 문서에서 정보를 추출
                        for (int j = 0; j < docs.length(); j++) {
                            JSONObject doc = docs.getJSONObject(j).getJSONObject("doc");
                            // 주제분류명(class_nm)을 가져옴
                            String class_nm = doc.getString("class_nm");
                            // 책 이름(bookname)을 가져옴
                            String bookName = doc.getString("bookname");
                            // 책 이미지 URL(bookImageURL)을 가져옴
                            String bookImageUrl = doc.getString("bookImageURL");
                            // 저자(authors)를 가져옴
                            String authors = doc.getString("authors");
                            String isbn13 = doc.getString("isbn13");
                            // 책 정보를 담은 SearchBook 객체 생성
                            SearchBook book = new SearchBook(class_nm, bookName, authors, bookImageUrl, isbn13);
                            // 생성한 SearchBook 객체를 리스트에 추가
                            books.add(book);
                        }
                    }
                    // 콜백을 통해 성공적인 응답 처리 (책 리스트 전달)
                    callback.onSuccess(books);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                } finally {
                    response.close();
                }
            }
        });
    } // end of hotTrend

    // 상세보기
    public void getDetailBook(String isbn13, final HttpResponseCallback<CompositeSearchBookDetail> callback) {
        String url = BASE_URL + "books/" + isbn13;
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
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    // JSON 응답을 파싱
                    JSONObject responseBody = new JSONObject(response.body().string());
                    JSONObject book = responseBody.getJSONObject("response").getJSONObject("book");

                    // 책 정보 추출
                    String bookName = book.getString("bookname");
                    String authors = book.getString("authors");
                    String publisher = book.getString("publisher");
                    String bookImageUrl = book.getString("bookImageURL");
                    String description = book.getString("description");
                    int publication_year = book.getInt("publication_year");
                    String isbn13 = book.getString("isbn13");
                    String class_no = book.getString("class_no");
                    String class_nm = book.getString("class_nm");
                    int loanCnt = book.has("loanCnt") ? book.getInt("loanCnt") : 0; // loanCnt 이후 데이터가 존재하지 않는 도서가 있어 기본값 0으로 설정
                    String vol = book.getString("vol");

                    // 대출 기록 정보 추출
                    JSONArray loanHistoryArray = responseBody.getJSONObject("response").getJSONArray("loanHistory");
                    List<String> month = new ArrayList<>();
                    List<String> loanHistoryCnt = new ArrayList<>();
                    List<String> ranking = new ArrayList<>();
                    for (int i = 0; i < loanHistoryArray.length(); i++) {
                        JSONObject loan = loanHistoryArray.getJSONObject(i).getJSONObject("loan"); // 첫 번째 항목만 사용
                        month.add(loan.getString("month"));
                        loanHistoryCnt.add(loan.getString("loanCnt"));
                        ranking.add(loan.getString("ranking"));
                    }

                    // 대출 그룹 정보 추출
                    JSONArray loanGrps = responseBody.getJSONObject("response").getJSONArray("loanGrps");
                    List<String> age = new ArrayList<>();
                    List<String> gender = new ArrayList<>();
                    List<String> loanGrpsCnt = new ArrayList<>();
                    List<String> loanGrpsRanking = new ArrayList<>();
                    for (int i = 0; i < loanGrps.length(); i++) {
                        JSONObject loanGrp = loanGrps.getJSONObject(i).getJSONObject("loanGrp");
                        age.add(loanGrp.getString("age"));
                        gender.add(loanGrp.getString("gender"));
                        loanGrpsCnt.add(loanGrp.getString("loanCnt"));
                        loanGrpsRanking.add(loanGrp.getString("ranking"));
                    }

                    // 키워드 정보 추출
                    JSONArray keywords = responseBody.getJSONObject("response").getJSONArray("keywords");
                    List<String> word = new ArrayList<>();
                    List<String> weight = new ArrayList<>();
                    for (int i = 0; i < keywords.length(); i++) {
                        JSONObject keyword = keywords.getJSONObject(i).getJSONObject("keyword");
                        word.add(keyword.getString("word"));
                        weight.add(keyword.getString("weight"));
                    }

                    // 마니아, 다독자 isbn13 추출
                    JSONArray maniaRecBooks = responseBody.getJSONObject("response").getJSONArray("maniaRecBooks");
                    List<String> maniaIsbn13 = new ArrayList<>();
                    for (int i = 0; i < maniaRecBooks.length(); i++) {
                        JSONObject maniaBook = maniaRecBooks.getJSONObject(i).getJSONObject("book");
                        maniaIsbn13.add(maniaBook.getString("isbn13"));
                    }

                    JSONArray readerRecBooks = responseBody.getJSONObject("response").getJSONArray("readerRecBooks");
                    List<String> readerIsbn13 = new ArrayList<>();
                    for (int i = 0; i < readerRecBooks.length(); i++) {
                        JSONObject readerBook = readerRecBooks.getJSONObject(i).getJSONObject("book");
                        readerIsbn13.add(readerBook.getString("isbn13"));
                    }

                    // 책 정보를 담은 SearchBookDetail 객체 생성
                    SearchBookDetail bookDetail = new SearchBookDetail(
                            bookName, authors, publisher, bookImageUrl, description, publication_year, isbn13, vol, class_no, class_nm, loanCnt,
                            month, loanHistoryCnt, ranking, age, gender, loanGrpsCnt, loanGrpsRanking, word, weight
                    );
                    SearchBookDetail maniaReaderBookDetail = new SearchBookDetail(maniaIsbn13, readerIsbn13);
                    // 객체 두개를 복합 객체로 만들어서 반환 -> callback.onSuccess(); 메소드가 객체를 한 번만 반환할 수 있어서.
                    CompositeSearchBookDetail compositeSearchBookDetail = new CompositeSearchBookDetail(bookDetail, maniaReaderBookDetail);
                    // 콜백을 통해 성공적인 응답 처리 (BookDetail 객체 전달)
                    callback.onSuccess(compositeSearchBookDetail);
                } catch (JSONException e) {
                    // JSON 파싱 중 예외가 발생한 경우 콜백을 통해 실패 처리
                    callback.onFailure(e);
                } finally {
                    response.close();
                }
            }
        });
    } // end of getDetailBook

    // 마니아 도서 조회
    public void getManiaRecBook(List<String> isbn13List, HttpResponseCallback<List<SearchBookDetail>> callback) {
        List<SearchBookDetail> maniaBooks = new ArrayList<>();
        int totalIsbn13 = isbn13List.size();
        for(String isbn13 : isbn13List) {
            String url = BASE_URL + "mania?isbn=" + isbn13;
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try{
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        JSONObject responseBody = new JSONObject(response.body().string());
                        JSONArray docsArray = responseBody.getJSONObject("response").getJSONArray("docs");

                        synchronized (maniaBooks) {
                            for (int i = 0; i < docsArray.length(); i++) {
                                JSONObject book = docsArray.getJSONObject(i).getJSONObject("book");

                                String bookName = book.getString("bookname");
                                String authors = book.getString("authors");
                                String bookImageUrl = book.getString("bookImageURL");
                                String isbn13 = book.getString("isbn13");

                                SearchBookDetail maniaBook = new SearchBookDetail(bookName, authors, bookImageUrl, isbn13);
                                maniaBooks.add(maniaBook);
                            }

                            if (maniaBooks.size() == totalIsbn13) {
                                callback.onSuccess(maniaBooks);
                            }
                        }
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    } finally {
                        response.close();
                    }
                }
            });
        }
    } // end of ManiaRecBook

    // 다독자 도서 조회
    public void getReaderRecBook(List<String> isbn13List, HttpResponseCallback<List<SearchBookDetail>> callback) {
        List<SearchBookDetail> readerBooks = new ArrayList<>();
        int totalIsbn13 = isbn13List.size();
        for(String isbn13 : isbn13List) {
            String url = BASE_URL + "reader?isbn=" + isbn13;
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        JSONObject responseBody = new JSONObject(response.body().string());
                        JSONArray docsArray = responseBody.getJSONObject("response").getJSONArray("docs");

                        synchronized (readerBooks) {
                            for (int i = 0; i < docsArray.length(); i++) {
                                JSONObject book = docsArray.getJSONObject(i).getJSONObject("book");

                                String bookName = book.getString("bookname");
                                String authors = book.getString("authors");
                                String bookImageUrl = book.getString("bookImageURL");
                                String isbn13 = book.getString("isbn13");

                                SearchBookDetail readerBook = new SearchBookDetail(bookName, authors, bookImageUrl, isbn13);
                                readerBooks.add(readerBook);
                            }

                            if (readerBooks.size() == totalIsbn13) {
                                callback.onSuccess(readerBooks);
                            }
                        }
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    } finally {
                        response.close();
                    }
                }
            });
        }
    } // end of getReaderRecBook
}
