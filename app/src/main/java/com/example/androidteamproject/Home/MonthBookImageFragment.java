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

public class MonthBookImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_BOOKNAME = "bookName";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_CLASS_NM = "class_nm";
    private static final String ARG_ISBN13 = "isbn13";
    private String imageUrl;
    private String bookName;
    private String authors;
    private String class_nm;
    private String isbn13;
    private MonthBookAdapter.OnItemClickListener onItemClickListener;

    public static MonthBookImageFragment newInstance(String class_nm, String bookName, String authors, String imageUrl, String isbn13, MonthBookAdapter.OnItemClickListener onItemClickListener) {
        MonthBookImageFragment fragment = new MonthBookImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_BOOKNAME, bookName);
        args.putString(ARG_AUTHORS, authors);
        args.putString(ARG_CLASS_NM, class_nm);
        args.putString(ARG_ISBN13, isbn13);
        fragment.setArguments(args);
        fragment.setOnItemClickListener(onItemClickListener);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            bookName = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            class_nm = getArguments().getString(ARG_CLASS_NM);
            isbn13 = getArguments().getString(ARG_ISBN13);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.month_book_img, container, false);
        ImageView monthBookImg = view.findViewById(R.id.iv_monthBookImg);
        TextView monthBookTitle = view.findViewById(R.id.tv_monthBookTitle);
        TextView monthBookAuthor = view.findViewById(R.id.tv_monthBookAuthor);
        TextView monthClassNm = view.findViewById(R.id.tv_monthBookClassName);

        if (getArguments() != null) {
            class_nm = getArguments().getString(ARG_CLASS_NM);
            bookName = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            isbn13 = getArguments().getString(ARG_ISBN13);
        }

        Picasso.get().load(imageUrl).into(monthBookImg);

        monthBookImg.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(isbn13, bookName, authors, imageUrl);
            }
        });

        monthBookTitle.setText(bookName);
        monthBookAuthor.setText(authors);
        monthClassNm.setText(class_nm);

        return view;
    }

    public void setOnItemClickListener(MonthBookAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
