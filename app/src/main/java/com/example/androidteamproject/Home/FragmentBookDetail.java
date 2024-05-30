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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidteamproject.ApiData.DataBase;
import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBookDetail;
import com.example.androidteamproject.R;
import com.example.androidteamproject.SessionManager;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    private static String API_KEY;

    DataBase dataBase = new DataBase();
    SessionManager sessionManager;

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
        API_KEY = context.getString(R.string.api_key);

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

        fetchBookDetail(isbn13, bookNameTextView, authorsTextView, descriptionTextView, bookImageView, publisherTextView, publicationYearTextView, isbnTextView, classNoTextView, classNmTextView, loanCntTextView, ageTextView, wordTextView);

        // 즐겨찾기 여부 확인
        checkFavoriteStatus();

        toggle_bookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new Thread(() -> {
                    if (isChecked) {
                        // 즐겨찾기에 추가
                        String result = dataBase.addFavorite(sessionManager.getMember(), bookId);
                        getActivity().runOnUiThread(() -> {
                            toggle_bookmark.setBackgroundResource(result.equals("즐겨찾기 추가 성공") ? R.drawable.ic_bookmark_on : R.drawable.ic_bookmark_off);
                        });
                    } else {
                        // 즐겨찾기에서 삭제
                        String result = dataBase.removeFavorite(sessionManager.getMember(), bookId);
                        getActivity().runOnUiThread(() -> {
                            toggle_bookmark.setBackgroundResource(result.equals("즐겨찾기 삭제 성공") ? R.drawable.ic_bookmark_off : R.drawable.ic_bookmark_on);
                        });
                    }
                }).start();
            }
        });

        return view;
    }

    private void fetchBookDetail(String isbn13, TextView bookNameTextView, TextView authorsTextView, TextView descriptionTextView, ImageView bookImageView, TextView publisherTextView, TextView publicationYearTextView, TextView isbnTextView, TextView classNoTextView, TextView classNmTextView, TextView loanCntTextView, TextView ageTextView, TextView wordTextView) {
        String url = "http://data4library.kr/api/usageAnalysisList?authKey=" + API_KEY + "&isbn13=" + isbn13 + "&format=json";

        HttpConnection.getInstance(getContext()).getDetailBook(url, new HttpConnection.HttpResponseCallback<SearchBookDetail>() {
            @Override
            public void onSuccess(SearchBookDetail bookDetail) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 로그 추가하여 데이터 확인
                        System.out.println("Book Detail: " + bookDetail.toString());

                        // 모든 데이터를 설정하기 전에 각 데이터에 로그를 추가하여 확인
                        bookNameTextView.setText(bookDetail.getBookName());
                        authorsTextView.setText(bookDetail.getAuthors());
                        descriptionTextView.setText(Html.fromHtml(bookDetail.getDescription(), Html.FROM_HTML_MODE_LEGACY)); // &lt; 와 같은 특수문자 변환을 위해 수정함
                        publisherTextView.setText(bookDetail.getPublisher());
                        publicationYearTextView.setText(String.valueOf(bookDetail.getPublication_year()));
                        isbnTextView.setText(bookDetail.getIsbn13());
                        classNoTextView.setText(bookDetail.getClass_no());
                        classNmTextView.setText(bookDetail.getClass_nm());
                        loanCntTextView.setText(String.valueOf(bookDetail.getLoanCnt())); // 정수 값을 문자열로 변환

                        // 월별 대출 정보 출력
                        List<String> month = bookDetail.getMonth();
                        List<String> loanHistoryCnt = bookDetail.getLoanHistoryCnt();

                        List<Entry> entries = new ArrayList<>();
                        for (int i = 0; i < month.size(); i++) {
                            entries.add(new Entry(i, Float.parseFloat(loanHistoryCnt.get(i))));
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

                        // 대출 그룹 정보 출력
                        List<String> age = bookDetail.getAge();
                        List<String> gender = bookDetail.getGender();
                        List<String> loanGrpsCnt = bookDetail.getLoanGrpsCnt();
                        List<String> loanGrpsRanking = bookDetail.getLoanGrpsRanking();
                        StringBuilder ageBuilder = new StringBuilder();
                        for (int i = 0; i < age.size(); i++) {
                            // ranking은 일단 생략함
                            ageBuilder.append(i+1 + "위 - " + age.get(i)).append(" ").append(gender.get(i)).append(" : ").append(loanGrpsCnt.get(i)).append("권\n");
                        }
                        ageTextView.setText(ageBuilder.toString());

                        // 키워드 정보 출력
                        List<String> word = bookDetail.getWord();
                        List<String> weight = bookDetail.getWeight();
                        List<KeywordWithWeight> keywords = new ArrayList<>();
                        for (int i = 0; i < word.size(); i++) {
                            keywords.add(new KeywordWithWeight(word.get(i), weight.get(i)));
                        }

                        Collections.shuffle(keywords);

                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        Random random = new Random();
                        for (KeywordWithWeight kw : keywords) {
                            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                            float size = 1.0f + (Float.parseFloat(kw.weight) / 10); // 가중치에 따라 크기를 조절합니다.
                            SpannableString keywordSpan = new SpannableString(kw.word + " ");
                            keywordSpan.setSpan(new ForegroundColorSpan(color), 0, keywordSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            keywordSpan.setSpan(new RelativeSizeSpan(size), 0, keywordSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableStringBuilder.append(keywordSpan);
                        }
                        wordTextView.setText(spannableStringBuilder);

                        // 도서 중복 확인 중복시 안하고 없을시 추가
                        SearchBookDetail searchBookDetail = new SearchBookDetail(bookDetail.getBookName(),bookDetail.getAuthors(),bookDetail.getPublisher(),bookDetail.getBookImageUrl(),bookDetail.getDescription(),bookDetail.getPublication_year(),bookDetail.getIsbn13(),bookDetail.getVol(),bookDetail.getClass_no(),bookDetail.getClass_nm(),bookDetail.getLoanCnt(),bookDetail.getMonth(),bookDetail.getLoanHistoryCnt(),bookDetail.getRankings(),bookDetail.getAge(),bookDetail.getGender(),bookDetail.getLoanGrpsCnt(),bookDetail.getLoanGrpsRanking(),bookDetail.getWord(),bookDetail.getWeight());
                        dataBase.insertBook(searchBookDetail);
                        // 도서 아디 찾기
                        int book_id = dataBase.selectBookId(searchBookDetail);
                        Log.e("cha",""+book_id);
                        // 검색 기록 도서pk확인 있으면 삭제하고 추가 없으면 추가
                        dataBase.insertHistory(sessionManager.getMember(),book_id);

                        // 학과별 검색횟수 있으면 업데이트 없으면 추가
                        int book_count_id = dataBase.findBookCount(book_id, sessionManager.getDepartmentId());
                        Log.e("check","book_count_id"+book_count_id);
                        if(book_count_id != 0){
                            dataBase.updateBookCount(book_count_id);
                        }else{
                            dataBase.insertBookCount(sessionManager.getDepartmentId(),book_id);
                        }


                        if (bookDetail.getBookImageUrl() != null && !bookDetail.getBookImageUrl().isEmpty()) {
                            Picasso.get().load(bookDetail.getBookImageUrl()).into(bookImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    System.out.println("Picasso: Image loaded successfully.");
                                }

                                @Override
                                public void onError(Exception e) {
                                    System.err.println("Picasso: Failed to load image. " + e.getMessage());
                                    bookImageView.setImageResource(R.drawable.ic_error); // Placeholder 이미지 설정
                                }
                            });
                        } else {
                            bookImageView.setImageResource(R.drawable.ic_error); // Placeholder 이미지 설정
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        descriptionTextView.setText("도서 상세 정보를 불러오지 못했습니다. 오류: " + e.getMessage());
                    });
                }
            }
        });
    }
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
            return month.get((int) value).substring(5);
        }
    }

    private void checkFavoriteStatus() {
        new Thread(() -> {
            bookId = dataBase.selectBookId(new SearchBookDetail(isbn13)); // isbn13을 기반으로 bookId 가져오기
            boolean isFavorite = dataBase.isFavorite(sessionManager.getMember(), bookId);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    toggle_bookmark.setChecked(isFavorite);
                    toggle_bookmark.setBackgroundResource(isFavorite ? R.drawable.ic_bookmark_on : R.drawable.ic_bookmark_off);
                });
            }
        }).start();
    }
}
