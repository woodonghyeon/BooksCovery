package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidteamproject.ApiData.HttpConnection;
import com.example.androidteamproject.ApiData.SearchBookDetail;
import com.example.androidteamproject.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        TextView loanHistoryCntTextView = view.findViewById(R.id.tv_detail_loan_history_cnt);
        TextView rankingTextView = view.findViewById(R.id.tv_detail_ranking);
        TextView ageTextView = view.findViewById(R.id.tv_detail_age);
        TextView genderTextView = view.findViewById(R.id.tv_detail_gender);
        TextView loanGrpsCntTextView = view.findViewById(R.id.tv_detail_loan_grps_cnt);
        TextView loanGrpsRankingTextView = view.findViewById(R.id.tv_detail_loan_grps_ranking);
        TextView wordTextView = view.findViewById(R.id.tv_detail_word);
        TextView weightTextView = view.findViewById(R.id.tv_detail_weight);

        // 로깅 추가
        System.out.println("FragmentBookDetail onCreateView: " + bookName + ", " + authors + ", " + imageUrl);

        Picasso.get().load(imageUrl).into(bookImageView, new Callback() {
            @Override
            public void onSuccess() {
                System.out.println("Picasso: Image loaded successfully.");
            }

            @Override
            public void onError(Exception e) {
                System.err.println("Picasso: Failed to load image. " + e.getMessage());
            }
        });

        bookNameTextView.setText(bookName);
        authorsTextView.setText(authors);

        fetchBookDetail(isbn13, bookNameTextView, authorsTextView, descriptionTextView, bookImageView, publisherTextView, publicationYearTextView, classNoTextView, classNmTextView, loanCntTextView, monthTextView, loanHistoryCntTextView, rankingTextView, ageTextView, genderTextView, loanGrpsCntTextView, loanGrpsRankingTextView, wordTextView, weightTextView);

        return view;
    }

    private void fetchBookDetail(String isbn13, TextView bookNameTextView, TextView authorsTextView, TextView descriptionTextView, ImageView bookImageView, TextView publisherTextView, TextView publicationYearTextView, TextView classNoTextView, TextView classNmTextView, TextView loanCntTextView, TextView monthTextView, TextView loanHistoryCntTextView, TextView rankingTextView, TextView ageTextView, TextView genderTextView, TextView loanGrpsCntTextView, TextView loanGrpsRankingTextView, TextView wordTextView, TextView weightTextView) {
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
                        descriptionTextView.setText(bookDetail.getDescription());

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
                        StringBuilder loanHistoryCntBuilder = new StringBuilder();
                        StringBuilder rankingBuilder = new StringBuilder();
                        for (int i = 0; i < month.size(); i++) {
                            monthBuilder.append(month.get(i)).append("\n");
                            loanHistoryCntBuilder.append(loanHistoryCnt.get(i)).append("\n");
                            rankingBuilder.append(ranking.get(i)).append("\n");
                        }
                        monthTextView.setText(monthBuilder.toString());
                        loanHistoryCntTextView.setText(loanHistoryCntBuilder.toString());
                        rankingTextView.setText(rankingBuilder.toString());

                        // 대출 그룹 정보 출력
                        List<String> age = bookDetail.getAge();
                        List<String> gender = bookDetail.getGender();
                        List<String> loanGrpsCnt = bookDetail.getLoanGrpsCnt();
                        List<String> loanGrpsRanking = bookDetail.getLoanGrpsRanking();
                        StringBuilder ageBuilder = new StringBuilder();
                        StringBuilder genderBuilder = new StringBuilder();
                        StringBuilder loanGrpsCntBuilder = new StringBuilder();
                        StringBuilder loanGrpsRankingBuilder = new StringBuilder();
                        for (int i = 0; i < age.size(); i++) {
                            ageBuilder.append(age.get(i)).append("\n");
                            genderBuilder.append(gender.get(i)).append("\n");
                            loanGrpsCntBuilder.append(loanGrpsCnt.get(i)).append("\n");
                            loanGrpsRankingBuilder.append(loanGrpsRanking.get(i)).append("\n");
                        }
                        ageTextView.setText(ageBuilder.toString());
                        genderTextView.setText(genderBuilder.toString());
                        loanGrpsCntTextView.setText(loanGrpsCntBuilder.toString());
                        loanGrpsRankingTextView.setText(loanGrpsRankingBuilder.toString());

                        // 키워드 정보 출력
                        List<String> word = bookDetail.getWord();
                        List<String> weight = bookDetail.getWeight();
                        StringBuilder wordBuilder = new StringBuilder();
                        StringBuilder weightBuilder = new StringBuilder();
                        for (int i = 0; i < word.size(); i++) {
                            wordBuilder.append(word.get(i)).append("\n");
                            weightBuilder.append(weight.get(i)).append("\n");
                        }
                        wordTextView.setText(wordBuilder.toString());
                        weightTextView.setText(weightBuilder.toString());

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
}
