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

import com.bumptech.glide.Glide;
import com.example.androidteamproject.R;

public class PopularBookFragment extends Fragment {
    private static final String ARG_BOOKNAME = "book_name";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_ISBN13 = "isbn13";
    private String imageUrl;
    private String bookName;
    private String authors;
    private String isbn13;
    private PopularBooksAdapter.OnItemClickListener onItemClickListener;

    public static PopularBookFragment newInstance(String bookName, String authors, String imageUrl, String isbn13, PopularBooksAdapter.OnItemClickListener onItemClickListener) {
        PopularBookFragment fragment = new PopularBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOKNAME, bookName);
        args.putString(ARG_AUTHORS, authors);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_ISBN13, isbn13);
        fragment.setOnItemClickListener(onItemClickListener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            bookName = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            isbn13 = getArguments().getString(ARG_ISBN13);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popular_book_img, container, false);

        TextView bookNameTextView = view.findViewById(R.id.tv_popular_book_name);
        TextView authorsTextView = view.findViewById(R.id.tv_popular_book_authors);
        ImageView bookImageView = view.findViewById(R.id.iv_popularBookImg);

        if (getArguments() != null) {
            bookName = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            isbn13 = getArguments().getString(ARG_ISBN13);
        }

        Glide.with(this)
                .load(imageUrl)
                .into(bookImageView);

        bookImageView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(isbn13, bookName, authors, imageUrl);
            }
        });

        bookNameTextView.setText(bookName);
        authorsTextView.setText(authors);

        return view;
    }

    public void setOnItemClickListener(PopularBooksAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

