package com.example.androidteamproject.Home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.androidteamproject.ApiData.CompositeSearchBookDetail;
import com.example.androidteamproject.ApiData.DataBase;
import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBookDetail;
import com.example.androidteamproject.R;
import com.example.androidteamproject.SessionManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

public class FragmentBookDetail extends Fragment {
    private static final String ARG_ISBN13 = "isbn13";
    private static final String ARG_BOOKNAME = "bookName";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_IMAGE_URL = "imageUrl";

    private String isbn13;
    private String bookName;
    private String authors;
    private String imageUrl;
    private int bookId;
    private LineChart lineChart;
    private ToggleButton toggle_bookmark;
    private ViewPager2 maniaBookPager, readerBookPager;
    private ManiaBookAdapter maniaBookAdapter;
    private ReaderBookAdapter readerBookAdapter;

    DataBase dataBase = new DataBase();
    SessionManager sessionManager;
    private boolean isFragmentActive;

    public static FragmentBookDetail newInstance(String isbn13, String bookName, String authors, String imageUrl) {
        FragmentBookDetail fragment = new FragmentBookDetail();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN13, isbn13);
        args.putString(ARG_BOOKNAME, bookName);
        args.putString(ARG_AUTHORS, authors);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isbn13 = getArguments().getString(ARG_ISBN13);
            bookName = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);
        Context context = getContext();
        sessionManager = new SessionManager(context);

        ImageView bookImageView = view.findViewById(R.id.iv_detail_book_image);
        TextView bookNameTextView = view.findViewById(R.id.tv_detail_book_name);
        TextView authorsTextView = view.findViewById(R.id.tv_detail_authors);
        TextView descriptionTextView = view.findViewById(R.id.tv_detail_description);
        TextView publisherTextView = view.findViewById(R.id.tv_detail_publisher);
        TextView publicationYearTextView = view.findViewById(R.id.tv_detail_publication_year);
        TextView isbnTextView = view.findViewById(R.id.tv_detail_isbn);
        TextView classNoTextView = view.findViewById(R.id.tv_detail_class_no);
        TextView classNmTextView = view.findViewById(R.id.tv_detail_class_nm);
        TextView loanCntTextView = view.findViewById(R.id.tv_detail_loan_cnt);
        TextView ageTextView = view.findViewById(R.id.tv_detail_age);
        TextView wordTextView = view.findViewById(R.id.tv_detail_word);
        toggle_bookmark = view.findViewById(R.id.toggle_bookmark);
        lineChart = view.findViewById(R.id.line_chart);
        maniaBookPager = view.findViewById(R.id.maniaBooks_viewpager);
        readerBookPager = view.findViewById(R.id.readerBooks_viewpager);

        // 로깅 추가
        System.out.println("FragmentBookDetail onCreateView: " + bookName + ", " + authors + ", " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_warring).error(R.drawable.ic_error).into(bookImageView, new Callback() {
                @Override
                public void onSuccess() {
                    System.out.println("Picasso: Image loaded successfully.");
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("Picasso: Failed to load image. " + e.getMessage());
                }
            });
        } else {
            bookImageView.setImageResource(R.drawable.ic_error);
        }

        bookNameTextView.setText(bookName != null ? bookName : "N/A");
        authorsTextView.setText(authors != null ? authors : "N/A");

        isFragmentActive = true;

        fetchBookDetail(isbn13, bookNameTextView, authorsTextView, descriptionTextView, bookImageView, publisherTextView, publicationYearTextView, isbnTextView, classNoTextView, classNmTextView, loanCntTextView, ageTextView, wordTextView);

        return view;
    }

    @Override
    public void onDestroyView() {
        isFragmentActive = false;
        super.onDestroyView();
    }

    private void fetchBookDetail(String isbn13, TextView bookNameTextView, TextView authorsTextView, TextView descriptionTextView, ImageView bookImageView, TextView publisherTextView, TextView publicationYearTextView, TextView isbnTextView, TextView classNoTextView, TextView classNmTextView, TextView loanCntTextView, TextView ageTextView, TextView wordTextView) {
        Integer memberId = sessionManager.getMember();
        Integer departmentId = sessionManager.getDepartmentId();

        HttpConnection.getInstance(getContext()).getDetailBook(isbn13, memberId, departmentId, new HttpConnection.HttpResponseCallback<CompositeSearchBookDetail>() {
            @Override
            public void onSuccess(CompositeSearchBookDetail responseData) {
                if (isFragmentActive && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        SearchBookDetail bookDetail = responseData.getBookDetail();
                        // 로그 추가하여 데이터 확인
                        System.out.println("Book Detail: " + bookDetail.toString());

                        // 모든 데이터를 설정하기 전에 각 데이터에 로그를 추가하여 확인
                        bookNameTextView.setText(bookDetail.getBookName());
                        authorsTextView.setText(bookDetail.getAuthors());
                        descriptionTextView.setText(Html.fromHtml(
                                // &lt; 와 같은 특수문자 변환을 위해 수정함
                                bookDetail.getDescription() != null ? bookDetail.getDescription() : "",
                                Html.FROM_HTML_MODE_LEGACY
                        ));
                        publisherTextView.setText(bookDetail.getPublisher());
                        publicationYearTextView.setText(String.valueOf(bookDetail.getPublication_year()));
                        isbnTextView.setText(bookDetail.getIsbn13());
                        classNoTextView.setText(bookDetail.getClass_no());
                        loanCntTextView.setText(String.valueOf(bookDetail.getLoanCnt())); // 정수 값을 문자열로 변환

                        // 월별 대출 정보 출력
                        List<String> month = bookDetail.getMonth();
                        List<String> loanHistoryCnt = bookDetail.getLoanHistoryCnt();

                        if (month != null && loanHistoryCnt != null) {
                            List<Entry> entries = new ArrayList<>();
                            synchronized (month) {
                                for (int i = 0; i < month.size(); i++) {
                                    entries.add(new Entry(i, Float.parseFloat(loanHistoryCnt.get(i))));
                                }
                            }

                            LineDataSet dataSet = new LineDataSet(entries, "월별 대출 건수");
                            dataSet.setColor(Color.CYAN); // 라인 색상 변경
                            dataSet.setCircleColor(Color.CYAN); // 데이터 점 색상 변경
                            dataSet.setLineWidth(2f); // 라인 두께 변경
                            dataSet.setCircleRadius(5f); // 데이터 점 크기 변경
                            dataSet.setDrawValues(false); // 데이터 값 라벨 제거

                            LineData lineData = new LineData(dataSet);
                            lineChart.setData(lineData);

                            XAxis xAxis = lineChart.getXAxis();
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setGranularity(1f);
                            xAxis.setValueFormatter(new XAxisFormatter(month));
                            xAxis.setLabelCount(month.size()); // 월별 라벨 표시

                            YAxis leftAxis = lineChart.getAxisLeft();
                            leftAxis.setDrawGridLines(false); // Y축 격자 제거
                            leftAxis.setDrawLabels(false); // Y축 라벨 제거
                            leftAxis.setDrawAxisLine(false); // Y축 선 제거

                            YAxis rightAxis = lineChart.getAxisRight();
                            rightAxis.setEnabled(false); // 오른쪽 Y축 비활성화

                            // 여백 추가
                            lineChart.setExtraOffsets(80, 50, 80, 0);

                            // 모든 선 제거
                            lineChart.getXAxis().setDrawLabels(false); // X축 라벨 제거
                            lineChart.getAxisLeft().setDrawAxisLine(false);
                            lineChart.getAxisLeft().setDrawGridLines(false);
                            lineChart.getAxisRight().setDrawAxisLine(false);
                            lineChart.getAxisRight().setDrawGridLines(false);
                            lineChart.getXAxis().setDrawAxisLine(false);
                            lineChart.getXAxis().setDrawGridLines(false); // grid 라인 제거
                            lineChart.getLegend().setEnabled(false); // 범례 제거
                            lineChart.getDescription().setEnabled(false); // 설명 라벨 제거
                            lineChart.setDrawBorders(false); // 테두리 제거

                            // 터치 기능 활성화/비활성화
                            lineChart.setTouchEnabled(true); // 터치 활성화(데이터 클릭시 표시하기 위함)
                            lineChart.setDragEnabled(false); // 드래그로 줌인, 줌아웃 가능해서 비활성화
                            lineChart.setScaleEnabled(false); // 줌 기능 비활성화

                            CustomMarkerView markerView = new CustomMarkerView(getContext(), R.layout.marker_view, month);
                            lineChart.setMarker(markerView);

                            // 차트 새로고침
                            lineChart.invalidate();
                        } else {
                            Log.e("FragmentBookDetail", "Month or loanHistoryCnt is null");
                        }

                        // 대출 그룹 정보 출력
                        List<String> age = bookDetail.getAge();
                        List<String> gender = bookDetail.getGender();
                        List<String> loanGrpsCnt = bookDetail.getLoanGrpsCnt();
                        List<String> loanGrpsRanking = bookDetail.getLoanGrpsRanking();
                        if (age != null && gender != null && loanGrpsCnt != null) {
                            StringBuilder ageBuilder = new StringBuilder();
                            synchronized (age) {
                                for (int i = 0; i < age.size(); i++) {
                                    // ranking은 일단 생략함
                                    ageBuilder.append(i + 1 + "위 - " + age.get(i)).append(" ").append(gender.get(i)).append(" : ").append(loanGrpsCnt.get(i)).append("권\n");
                                }
                            }
                            ageTextView.setText(ageBuilder.toString());
                        } else {
                            Log.e("FragmentBookDetail", "Age, gender, or loanGrpsCnt is null");
                        }

                        // 키워드 정보 출력
                        List<String> word = bookDetail.getWord();
                        List<String> weight = bookDetail.getWeight();
                        if (word != null && weight != null) {
                            List<KeywordWithWeight> keywords = new ArrayList<>();
                            synchronized (word) {
                                for (int i = 0; i < word.size(); i++) {
                                    keywords.add(new KeywordWithWeight(word.get(i), weight.get(i)));
                                }
                            }
                            Collections.shuffle(keywords);

                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                            Random random = new Random();
                            synchronized (keywords) {
                                for (KeywordWithWeight kw : keywords) {
                                    int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                                    float size = 1.0f + (Float.parseFloat(kw.weight) / 10); // 가중치에 따라 크기를 조절합니다.
                                    SpannableString keywordSpan = new SpannableString(kw.word + " ");
                                    keywordSpan.setSpan(new ForegroundColorSpan(color), 0, keywordSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    keywordSpan.setSpan(new RelativeSizeSpan(size), 0, keywordSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannableStringBuilder.append(keywordSpan);
                                }
                            }
                            wordTextView.setText(spannableStringBuilder);
                        } else {
                            Log.e("FragmentBookDetail", "Word or weight is null");
                        }

                        // 도서 중복 확인 중복시 안하고 없을시 추가
                        dataBase.insertBook(bookDetail, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("DataBase", "Error inserting book", e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d("DataBase", "Book inserted successfully");
                                } else {
                                    Log.e("DataBase", "Failed to insert book: " + response.code());
                                }
                            }
                        });

                        // 도서 아이디 찾기
                        dataBase.selectBookId(bookDetail.getIsbn13(), new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("DataBase", "Error selecting book id", e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    String responseBody = response.body().string();
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        // book_id가 null인지 확인
                                        if (jsonObject.isNull("book_id")) {
                                            Log.e("DataBase", "book_id is null");
                                        } else {
                                            int book_id = jsonObject.getInt("book_id");

                                            // 검색 기록 도서pk확인 있으면 삭제하고 추가 없으면 추가
                                            dataBase.insertHistory(sessionManager.getMember(), book_id, new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    Log.e("DataBase", "Error inserting history", e);
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    if (response.isSuccessful()) {
                                                        Log.d("DataBase", "History inserted successfully");
                                                    } else {
                                                        Log.e("DataBase", "Failed to insert history: " + response.code());
                                                    }
                                                }
                                            });

                                            // 학과별 검색횟수 있으면 업데이트 없으면 추가
                                            dataBase.findBookCount(book_id, sessionManager.getDepartmentId(), new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    Log.e("DataBase", "Error finding book count", e);
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    if (response.isSuccessful()) {
                                                        String responseBody = response.body().string();
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(responseBody);
                                                            int book_count_id = jsonObject.getInt("book_count_id");
                                                            Log.e("check", "book_count_id" + book_count_id);

                                                            if (book_count_id != 0) {
                                                                dataBase.updateBookCount(book_count_id, new okhttp3.Callback() {
                                                                    @Override
                                                                    public void onFailure(Call call, IOException e) {
                                                                        Log.e("DataBase", "Error updating book count", e);
                                                                    }

                                                                    @Override
                                                                    public void onResponse(Call call, Response response) throws IOException {
                                                                        if (response.isSuccessful()) {
                                                                            Log.d("DataBase", "Book count updated successfully");
                                                                        } else {
                                                                            Log.e("DataBase", "Failed to update book count: " + response.code());
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                dataBase.insertBookCount(sessionManager.getDepartmentId(), book_id, new okhttp3.Callback() {
                                                                    @Override
                                                                    public void onFailure(Call call, IOException e) {
                                                                        Log.e("DataBase", "Error inserting book count", e);
                                                                    }

                                                                    @Override
                                                                    public void onResponse(Call call, Response response) throws IOException {
                                                                        if (response.isSuccessful()) {
                                                                            Log.d("DataBase", "Book count inserted successfully");
                                                                        } else {
                                                                            Log.e("DataBase", "Failed to insert book count: " + response.code());
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        Log.e("DataBase", "Failed to find book count: " + response.code());
                                                    }
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("DataBase", "Failed to select book id: " + response.code());
                                }
                            }
                        });

                        // 마니아, 다독자 추천 도서 불러오기
                        List<String> maniaIsbn13 = responseData.getManiaReaderBookDetail().getManiaIsbn13List();
                        List<String> readerIsbn13 = responseData.getManiaReaderBookDetail().getReaderIsbn13List();

                        getManiaRecBookItems(maniaIsbn13);
                        getReaderRecBookItems(readerIsbn13);

                        checkFavoriteStatus(bookDetail.getIsbn13(), memberId);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                if (isFragmentActive && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        descriptionTextView.setText("도서 상세 정보를 불러오지 못했습니다. 오류: " + e.getMessage());
                    });
                }
            }
        });
    } // end of fetchBookDetail

    private void checkFavoriteStatus(String isbn13, Integer memberId) {
        new Thread(() -> {
            dataBase.selectBookId(isbn13, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("DataBase", "Error selecting book id", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            if (jsonObject.isNull("book_id")) {
                                Log.e("DataBase", "book_id is null");
                            } else {
                                int bookId = jsonObject.getInt("book_id");
                                dataBase.isFavorite(memberId, bookId, new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.e("DataBase", "Error checking favorite", e);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            String responseBody = response.body().string();
                                            try {
                                                JSONObject jsonObject = new JSONObject(responseBody);
                                                boolean isFavorite = jsonObject.getBoolean("isFavorite");
                                                if (isFragmentActive && getActivity() != null) {
                                                    getActivity().runOnUiThread(() -> {
                                                        toggle_bookmark.setChecked(isFavorite);
                                                        toggle_bookmark.setBackgroundResource(isFavorite ? R.drawable.ic_bookmark_on : R.drawable.ic_bookmark_off);

                                                        toggle_bookmark.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                                            if (isChecked) {
                                                                dataBase.addFavorite(memberId, bookId, new okhttp3.Callback() {
                                                                    @Override
                                                                    public void onFailure(Call call, IOException e) {
                                                                        Log.e("DataBase", "Error adding favorite", e);
                                                                    }

                                                                    @Override
                                                                    public void onResponse(Call call, Response response) throws IOException {
                                                                        if (response.isSuccessful()) {
                                                                            Log.d("DataBase", "Favorite added successfully");
                                                                        } else {
                                                                            Log.e("DataBase", "Failed to add favorite: " + response.code());
                                                                        }
                                                                    }
                                                                });
                                                                toggle_bookmark.setBackgroundResource(R.drawable.ic_bookmark_on);
                                                            } else {
                                                                dataBase.removeFavorite(memberId, bookId, new okhttp3.Callback() {
                                                                    @Override
                                                                    public void onFailure(Call call, IOException e) {
                                                                        Log.e("DataBase", "Error removing favorite", e);
                                                                    }

                                                                    @Override
                                                                    public void onResponse(Call call, Response response) throws IOException {
                                                                        if (response.isSuccessful()) {
                                                                            Log.d("DataBase", "Favorite removed successfully");
                                                                        } else {
                                                                            Log.e("DataBase", "Failed to remove favorite: " + response.code());
                                                                        }
                                                                    }
                                                                });
                                                                toggle_bookmark.setBackgroundResource(R.drawable.ic_bookmark_off);
                                                            }
                                                        });
                                                    });
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Log.e("DataBase", "Failed to check favorite: " + response.code());
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("DataBase", "Failed to select book id: " + response.code());
                    }
                }
            });
        }).start();
    } // end of checkFavoriteStatus

    // 마니아
    private void getManiaRecBookItems(List<String> isbn13List) {
        HttpConnection.getInstance(getContext()).getManiaRecBook(isbn13List, new HttpConnection.HttpResponseCallback<List<SearchBookDetail>>() {
            @Override
            public void onSuccess(List<SearchBookDetail> maniaBooks) {
                if (isFragmentActive && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        List<String> bookNames = new ArrayList<>();
                        List<String> imageUrls = new ArrayList<>();
                        List<String> isbn13s = new ArrayList<>();
                        List<String> authors = new ArrayList<>();

                        synchronized (maniaBooks) {
                            for (SearchBookDetail book : maniaBooks) {
                                bookNames.add(book.getManiaBookName());
                                authors.add(book.getManiaAuthor());
                                imageUrls.add(book.getManiaImageUrl());
                                isbn13s.add(book.getManiaIsbn13());
                            }
                        }
                        setupManiaViewPager(bookNames, authors, imageUrls, isbn13s);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    } // end of getManiaRecBookItems

    // 마니아 ViewPager2
    public void setupManiaViewPager(List<String> bookNames, List<String> authors, List<String> imageUrls, List<String> isbn13s) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 없는 경우 처리
            return;
        }
        // 이미지 URL 있는 경우 설정
        if (getView() == null) {
            return;
        }
        maniaBookPager = getView().findViewById(R.id.maniaBooks_viewpager);
        maniaBookAdapter = new ManiaBookAdapter(requireActivity(), bookNames, authors, imageUrls, isbn13s, this::showBookDetail);
        maniaBookPager.setAdapter(maniaBookAdapter);
        maniaBookPager.setCurrentItem(1000);
        maniaBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        maniaBookPager.setCurrentItem(startPos);

        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.maniaBookPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.maniaBookPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        maniaBookPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));
        maniaBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    maniaBookPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    } // end of setupViewPager

    // 다독자
    private void getReaderRecBookItems(List<String> isbn13List) {
        HttpConnection.getInstance(getContext()).getReaderRecBook(isbn13List, new HttpConnection.HttpResponseCallback<List<SearchBookDetail>>() {
            @Override
            public void onSuccess(List<SearchBookDetail> readerBooks) {
                if (isFragmentActive && getActivity() != null) {
                    System.out.println("다독자 readerBooks(FragmentBookDetail) : " + readerBooks);
                    getActivity().runOnUiThread(() -> {
                        List<String> bookNames = new ArrayList<>();
                        List<String> imageUrls = new ArrayList<>();
                        List<String> isbn13s = new ArrayList<>();
                        List<String> authors = new ArrayList<>();

                        for (SearchBookDetail book : readerBooks) {
                            bookNames.add(book.getReaderBookName());
                            authors.add(book.getReaderAuthor());
                            imageUrls.add(book.getReaderImageUrl());
                            isbn13s.add(book.getReaderIsbn13());
                        }
                        setupReaderViewPager(bookNames, authors, imageUrls, isbn13s);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    } // end of getReaderRecBookItems

    // 다독자 ViewPager2
    public void setupReaderViewPager(List<String> bookNames, List<String> authors, List<String> imageUrls, List<String> isbn13s) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            // 이미지 없는 경우 처리
            return;
        }
        // 이미지 URL 있는 경우 설정
        if (getView() == null) {
            return;
        }
        readerBookPager = getView().findViewById(R.id.readerBooks_viewpager);
        readerBookAdapter = new ReaderBookAdapter(requireActivity(), bookNames, authors, imageUrls, isbn13s, this::showBookDetail);
        readerBookPager.setAdapter(readerBookAdapter);
        readerBookPager.setCurrentItem(1000);
        readerBookPager.setOffscreenPageLimit(10);

        int startPos = imageUrls.size() / 2;
        readerBookPager.setCurrentItem(startPos);

        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.maniaBookPageMargin);
        int pagerWidth = getResources().getDimensionPixelOffset(R.dimen.maniaBookPageWidth);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int offsetPx = screenWidth - pageMarginPx - pagerWidth;

        readerBookPager.setPageTransformer((page, position) -> page.setTranslationX(position * -offsetPx));
        readerBookPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    readerBookPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    } // end of setupViewPager

    private static class KeywordWithWeight {
        String word;
        String weight;

        KeywordWithWeight(String word, String weight) {
            this.word = word;
            this.weight = weight;
        }
    }

    public class XAxisFormatter extends ValueFormatter {
        private final List<String> month;

        public XAxisFormatter(List<String> month) {
            this.month = month;
        }

        @Override
        public String getFormattedValue(float value) {
            int index = (int) value;
            if (index < 0 || index >= month.size()) {
                // 유효하지 않은 인덱스 처리
                return "";
            }
            return month.get(index).substring(5);
        }
    }

    // showBookDetail
    private void showBookDetail(String isbn13, String bookName, String authors, String imageUrl) {
        // 새로운 FragmentBookDetail 인스턴스를 생성하고 필요한 데이터를 전달
        FragmentBookDetail fragment = FragmentBookDetail.newInstance(isbn13, bookName, authors, imageUrl);

        // FragmentTransaction을 통해 프래그먼트를 관리
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // 현재 프래그먼트를 가져와서 숨김
        Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.ly_home);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // 새로운 프래그먼트를 추가
        transaction.add(R.id.ly_home, fragment);
        transaction.addToBackStack(null); // 백스택에 추가하여 뒤로가기 버튼을 눌렀을 때 이전 프래그먼트로 돌아갈 수 있음
        transaction.commit();
    }
}
