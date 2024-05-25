package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.androidteamproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

public class BookDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_ISBN = "isbn13";
    private static final String ARG_BOOK_NAME = "book_name";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_IMAGE_URL = "image_url";

    private String isbn13;
    private String bookName;
    private String authors;
    private String imageUrl;

    public static BookDetailBottomSheet newInstance(String isbn13, String bookName, String authors, String imageUrl) {
        BookDetailBottomSheet fragment = new BookDetailBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN, isbn13);
        args.putString(ARG_BOOK_NAME, bookName);
        args.putString(ARG_AUTHORS, authors);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail_bottom_sheet, container, false);

        if (getArguments() != null) {
            isbn13 = getArguments().getString(ARG_ISBN);
            bookName = getArguments().getString(ARG_BOOK_NAME);
            authors = getArguments().getString(ARG_AUTHORS);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }

        ImageView bookImage = view.findViewById(R.id.iv_detail_book_image);
        TextView bookNameView = view.findViewById(R.id.iv_detail_book_name);
        TextView authorsView = view.findViewById(R.id.iv_detail_authors);

        Picasso.get().load(imageUrl).into(bookImage);
        bookNameView.setText(bookName);
        authorsView.setText(authors);

        return view;
    }
}
