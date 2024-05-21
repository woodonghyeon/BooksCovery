package com.example.androidteamproject.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.androidteamproject.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class LatelySearchImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_BOOKNAME = "bookName";
    private String imageUrl;
    private String bookName;

    public static LatelySearchImageFragment newInstance(String bookName, String imageUrl) {
        LatelySearchImageFragment fragment = new LatelySearchImageFragment();
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
        View view = inflater.inflate(R.layout.lately_search_book_img, container, false);
        CircleImageView imageView = view.findViewById(R.id.iv_latelySearch);
        TextView textView = view.findViewById(R.id.tv_latelySearch);

        // Glide를 사용하여 이미지 로드
        Glide.with(this).load(imageUrl).override(Target.SIZE_ORIGINAL).error(R.drawable.ic_error).into(imageView);
        textView.setText(bookName);

        return view;
    }
}
