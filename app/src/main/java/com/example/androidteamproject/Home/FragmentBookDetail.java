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

public class FragmentBookDetail extends Fragment {
    private static final String ARG_ISBN13 = "isbn13";
    private static final String ARG_BOOKNAME = "bookName";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_IMAGE_URL = "imageUrl";

    private String isbn13;
    private String bookName;
    private String authors;
    private String imageUrl;
    private static String API_KEY = "cc355482ccb755beacd4ba6f7134c20c6b59a237e1ee656a155a6ed3a2003941";

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

                        System.out.println("Setting month: " + bookDetail.getMonth());
                        monthTextView.setText(bookDetail.getMonth());

                        System.out.println("Setting loanHistoryCnt: " + bookDetail.getLoanHistoryCnt());
                        loanHistoryCntTextView.setText(bookDetail.getLoanHistoryCnt());

                        System.out.println("Setting ranking: " + bookDetail.getRanking());
                        rankingTextView.setText(bookDetail.getRanking());

                        System.out.println("Setting age: " + bookDetail.getAge());
                        ageTextView.setText(bookDetail.getAge());

                        System.out.println("Setting gender: " + bookDetail.getGender());
                        genderTextView.setText(bookDetail.getGender());

                        System.out.println("Setting loanGrpsCnt: " + bookDetail.getLoanGrpsCnt());
                        loanGrpsCntTextView.setText(bookDetail.getLoanGrpsCnt());

                        System.out.println("Setting loanGrpsRanking: " + bookDetail.getLoanGrpsRanking());
                        loanGrpsRankingTextView.setText(bookDetail.getLoanGrpsRanking());

                        System.out.println("Setting word: " + bookDetail.getWord());
                        wordTextView.setText(bookDetail.getWord());

                        System.out.println("Setting weight: " + bookDetail.getWeight());
                        weightTextView.setText(bookDetail.getWeight());

                        // 수정: 추가된 이미지 로딩 로직
                        if (bookDetail.getBookImageUrl() != null && !bookDetail.getBookImageUrl().isEmpty()) {
                            Picasso.get().load(bookDetail.getBookImageUrl()).into(bookImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    System.out.println("Picasso: Image loaded successfully.");
                                }

                                @Override
                                public void onError(Exception e) {
                                    System.err.println("Picasso: Failed to load image. " + e.getMessage());
                                }
                            });
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
