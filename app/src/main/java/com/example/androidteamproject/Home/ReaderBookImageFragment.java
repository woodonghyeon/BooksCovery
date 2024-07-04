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

import com.example.androidteamproject.R;
import com.squareup.picasso.Picasso;

public class ReaderBookImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_BOOKNAME = "bookName";
    private static final String ARG_ISBN13 = "isbn13";
    private String imageUrls;
    private String authors;
    private String bookNames;
    private String isbn13s;
    private ReaderBookAdapter.OnItemClickListener onItemClickListener;

    public static ReaderBookImageFragment newInstance(String bookName, String authors, String imageUrl, String isbn13, ReaderBookAdapter.OnItemClickListener onItemClickListener) {
        ReaderBookImageFragment fragment = new ReaderBookImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_BOOKNAME, bookName);
        args.putString(ARG_AUTHORS, authors);
        args.putString(ARG_ISBN13, isbn13);
        fragment.setArguments(args);
        fragment.setOnItemClickListener(onItemClickListener);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrls = getArguments().getString(ARG_IMAGE_URL);
            bookNames = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            isbn13s = getArguments().getString(ARG_ISBN13);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_book_img, container, false);
        ImageView readerBookImg = view.findViewById(R.id.iv_readerBookImg);
        TextView readerBookTitle = view.findViewById(R.id.tv_readerBookTitle);

        if (getArguments() != null) {
            bookNames = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            imageUrls = getArguments().getString(ARG_IMAGE_URL);
            isbn13s = getArguments().getString(ARG_ISBN13);
        }

        if (imageUrls == null || imageUrls.isEmpty()) {
            readerBookImg.setImageResource(R.drawable.ic_error);
        } else {
            Picasso.get().load(imageUrls).into(readerBookImg);
        }

        readerBookImg.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(isbn13s, bookNames, authors, imageUrls);
            }
        });

        readerBookTitle.setText(bookNames);

        return view;
    }

    public void setOnItemClickListener(ReaderBookAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
