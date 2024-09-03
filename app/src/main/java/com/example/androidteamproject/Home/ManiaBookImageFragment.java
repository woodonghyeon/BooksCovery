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

public class ManiaBookImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_BOOKNAME = "bookName";
    private static final String ARG_ISBN13 = "isbn13";
    private String imageUrls;
    private String authors;
    private String bookNames;
    private String isbn13s;
    private ManiaBookAdapter.OnItemClickListener onItemClickListener;

    public static ManiaBookImageFragment newInstance(String bookName, String authors, String imageUrl, String isbn13, ManiaBookAdapter.OnItemClickListener onItemClickListener) {
        ManiaBookImageFragment fragment = new ManiaBookImageFragment();
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
            isbn13s = getArguments().getString(ARG_ISBN13);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mania_book_img, container, false);
        ImageView maniaBookImg = view.findViewById(R.id.iv_maniaBookImg);
        TextView maniaBookTitle = view.findViewById(R.id.tv_maniaBookTitle);

        if (getArguments() != null) {
            bookNames = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            imageUrls = getArguments().getString(ARG_IMAGE_URL);
            isbn13s = getArguments().getString(ARG_ISBN13);
        }

        if (imageUrls.isEmpty()) {
            maniaBookImg.setImageResource(R.drawable.ic_error);
        } else {
            Picasso.get().load(imageUrls).into(maniaBookImg);
        }

        maniaBookImg.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(isbn13s, bookNames, authors, imageUrls);
            }
        });

        maniaBookTitle.setText(bookNames);

        return view;
    }

    public void setOnItemClickListener(ManiaBookAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
