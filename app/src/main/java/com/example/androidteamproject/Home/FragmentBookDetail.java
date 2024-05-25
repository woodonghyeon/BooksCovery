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
import com.example.androidteamproject.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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

        Picasso.get().load(imageUrl).into(bookImageView);
        bookNameTextView.setText(bookName);
        authorsTextView.setText(authors);

        fetchBookDetail(isbn13, descriptionTextView);

        return view;
    }

    private void fetchBookDetail(String isbn13, TextView descriptionTextView) {
        String url = "http://data4library.kr/api/usageAnalysisList?authKey=" + API_KEY + "&isbn13=" + isbn13 + "&format=json";

        HttpConnection.getInstance(getContext()).getDetailBook(url, new HttpConnection.HttpResponseCallback<String>() {
            @Override
            public void onSuccess(String responseData) {
                // 백그라운드 스레드에서 메인 스레드로 UI 업데이트
                getActivity().runOnUiThread(() -> {
                    try {
                        // 응답 데이터 로그로 출력
                        System.out.println("Response Data: " + responseData);
                        JSONObject jsonObject = new JSONObject(responseData);

                        if (jsonObject.has("response")) {
                            JSONObject responseObject = jsonObject.getJSONObject("response");
                            if (responseObject.has("book")) {
                                JSONObject bookObject = responseObject.getJSONObject("book");
                                String description = bookObject.getString("description");
                                descriptionTextView.setText(description);
                            } else {
                                descriptionTextView.setText("도서 상세 정보를 불러오지 못했습니다.");
                            }
                        } else {
                            descriptionTextView.setText("도서 상세 정보를 불러오지 못했습니다.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        descriptionTextView.setText("도서 상세 정보를 불러오지 못했습니다.");
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                getActivity().runOnUiThread(() -> {
                    e.printStackTrace();
                    descriptionTextView.setText("도서 상세 정보를 불러오지 못했습니다.");
                });
            }
        });
    }
}
