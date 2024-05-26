package com.example.androidteamproject.Home;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidteamproject.ApiData.DataBase;
import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBookDetail;
import com.example.androidteamproject.R;
import com.example.androidteamproject.SessionManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
    private static String API_KEY = "***REMOVED***";

    DataBase dataBase = new DataBase();

    private SessionManager sessionManager;

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
        sessionManager = new SessionManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        ImageView bookImageView = view.findViewById(R.id.iv_detail_book_image);
        TextView bookNameTextView = view.findViewById(R.id.tv_detail_book_name);
        TextView authorsTextView = view.findViewById(R.id.tv_detail_authors);
        TextView descriptionTextView = view.findViewById(R.id.tv_detail_description);
        TextView publisherTextView = view.findViewById(R.id.tv_detail_publisher);
        TextView publicationYearTextView = view.findViewById(R.id.tv_detail_publication_year);
        TextView classNoTextView = view.findViewById(R.id.tv_detail_class_no);
        TextView classNmTextView = view.findViewById(R.id.tv_detail_class_nm);
        TextView loanCntTextView = view.findViewById(R.id.tv_detail_loan_cnt);
        TextView monthTextView = view.findViewById(R.id.tv_detail_month);
        TextView ageTextView = view.findViewById(R.id.tv_detail_age);
        TextView wordTextView = view.findViewById(R.id.tv_detail_word);

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

        bookNameTextView.setText(bookName);
        authorsTextView.setText(authors);

        fetchBookDetail(isbn13, bookNameTextView, authorsTextView, descriptionTextView, bookImageView, publisherTextView, publicationYearTextView, classNoTextView, classNmTextView, loanCntTextView, monthTextView, ageTextView, wordTextView);

        return view;
    }

    private void fetchBookDetail(String isbn13, TextView bookNameTextView, TextView authorsTextView, TextView descriptionTextView, ImageView bookImageView, TextView publisherTextView, TextView publicationYearTextView, TextView classNoTextView, TextView classNmTextView, TextView loanCntTextView, TextView monthTextView, TextView ageTextView, TextView wordTextView) {
        String url = "http://data4library.kr/api/usageAnalysisList?authKey=" + API_KEY + "&isbn13=" + isbn13 + "&format=json";

        HttpConnection.getInstance(getContext()).getDetailBook(url, new HttpConnection.HttpResponseCallback<SearchBookDetail>() {
            @Override
            public void onSuccess(SearchBookDetail bookDetail) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 로그 추가하여 데이터 확인
                        System.out.println("Book Detail: " + bookDetail.toString());

                        // 모든 데이터를 설정하기 전에 각 데이터에 로그를 추가하여 확인
                        System.out.println("Setting bookName: " + bookDetail.getBookName());
                        bookNameTextView.setText(bookDetail.getBookName());

                        System.out.println("Setting authors: " + bookDetail.getAuthors());
                        authorsTextView.setText(bookDetail.getAuthors());

                        System.out.println("Setting description: " + bookDetail.getDescription());
                        descriptionTextView.setText(Html.fromHtml(bookDetail.getDescription(), Html.FROM_HTML_MODE_LEGACY)); // &lt; 와 같은 특수문자 변환을 위해 수정함

                        System.out.println("Setting publisher: " + bookDetail.getPublisher());
                        publisherTextView.setText(bookDetail.getPublisher());

                        System.out.println("Setting publication_year: " + bookDetail.getPublication_year());
                        publicationYearTextView.setText(bookDetail.getPublication_year());

                        System.out.println("Setting class_no: " + bookDetail.getClass_no());
                        classNoTextView.setText(bookDetail.getClass_no());

                        System.out.println("Setting class_nm: " + bookDetail.getClass_nm());
                        classNmTextView.setText(bookDetail.getClass_nm());

                        System.out.println("Setting loanCnt: " + bookDetail.getLoanCnt());
                        loanCntTextView.setText(bookDetail.getLoanCnt());

                        // 월별 대출 정보 출력
                        List<String> month = bookDetail.getMonth();
                        List<String> loanHistoryCnt = bookDetail.getLoanHistoryCnt();
                        List<String> ranking = bookDetail.getRankings();
                        StringBuilder monthBuilder = new StringBuilder();
                        for (int i = 0; i < month.size(); i++) {
                            // ranking은 일단 생략함
                            monthBuilder.append(month.get(i)).append(" : ").append(loanHistoryCnt.get(i)).append("권\n");
                        }
                        monthTextView.setText(monthBuilder.toString());

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
                        // 검색 기록 도서pk확인 있으면 삭제하고 추가 없으면 추가

                        //// 즐겨찾기 온클릭시 즐겨찾기 추가

                        // 학과별 검색횟수 있으면 업데이트 없으면 추가


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
}
