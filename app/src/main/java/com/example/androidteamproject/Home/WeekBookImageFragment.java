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
import com.bumptech.glide.request.target.Target;
import com.example.androidteamproject.R;

public class WeekBookImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_BOOKNAME = "bookName";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_CLASS_NM = "class_nm";
    private String imageUrl;
    private String bookName;
    private String authors;
    private String class_nm;

    public static WeekBookImageFragment newInstance(String class_nm, String bookName, String authors, String imageUrl) {
        WeekBookImageFragment fragment = new WeekBookImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_BOOKNAME, bookName);
        args.putString(ARG_AUTHORS, authors);
        args.putString(ARG_CLASS_NM, class_nm);
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
            class_nm = getArguments().getString(ARG_CLASS_NM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.week_book_img, container, false);
        ImageView weekBookImg = view.findViewById(R.id.iv_weekBookImg);
        TextView weekBookTitle = view.findViewById(R.id.tv_weekBookTitle);
        TextView weekBookAuthor = view.findViewById(R.id.tv_weekBookAuthor);
        TextView weekBookClassNm = view.findViewById(R.id.tv_weekBookClassName);

        // Glide를 사용하여 이미지 로드
        Glide.with(this).load(imageUrl).override(Target.SIZE_ORIGINAL).error(R.drawable.ic_error).into(weekBookImg);
        weekBookTitle.setText(bookName);
        weekBookAuthor.setText(authors);
        weekBookClassNm.setText(class_nm);

        return view;
    }
}
