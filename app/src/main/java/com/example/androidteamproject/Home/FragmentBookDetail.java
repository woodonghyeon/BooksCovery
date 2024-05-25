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

        fetchBookDetail(isbn13, bookNameTextView, authorsTextView, descriptionTextView, bookImageView);

        return view;
    }

    private void fetchBookDetail(String isbn13, TextView bookNameTextView, TextView authorsTextView, TextView descriptionTextView, ImageView bookImageView) {
        String url = "http://data4library.kr/api/usageAnalysisList?authKey=" + API_KEY + "&isbn13=" + isbn13 + "&format=json";

        HttpConnection.getInstance(getContext()).getDetailBook(url, new HttpConnection.HttpResponseCallback<SearchBookDetail>() {
            @Override
            public void onSuccess(SearchBookDetail bookDetail) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 로그 추가하여 데이터 확인
                        System.out.println("Book Detail: " + bookDetail.toString());
                        // 모든 데이터를 설정
                        bookNameTextView.setText(bookDetail.getBookName());
                        authorsTextView.setText(bookDetail.getAuthors());
                        descriptionTextView.setText(bookDetail.getDescription());
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
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        descriptionTextView.setText("도서 상세 정보를 불러오지 못했습니다.");
                    });
                }
            }
        });
    }
}
