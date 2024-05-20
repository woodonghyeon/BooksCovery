package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.androidteamproject.R;
import com.example.androidteamproject.Search.LatelySearchImageFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class WeekBookImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_BOOKNAME = "bookName";
    private String imageUrl;
    private String bookName;

    public static WeekBookImageFragment newInstance(String bookName, String imageUrl) {
        WeekBookImageFragment fragment = new WeekBookImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_BOOKNAME, bookName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            bookName = getArguments().getString(ARG_BOOKNAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.week_book_img, container, false);
        CircleImageView imageView = view.findViewById(R.id.iv_weekBookImg);
        TextView textView = view.findViewById(R.id.tv_weekBook);

        // Glide를 사용하여 이미지 로드
        Glide.with(this).load(imageUrl).into(imageView);
        textView.setText(bookName);

        return view;
    }
}
