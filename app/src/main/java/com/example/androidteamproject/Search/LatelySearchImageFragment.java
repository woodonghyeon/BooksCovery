package com.example.androidteamproject.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.androidteamproject.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class LatelySearchImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private String imageUrl;

    public static LatelySearchImageFragment newInstance(String imageUrl) {
        LatelySearchImageFragment fragment = new LatelySearchImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lately_search_book_img, container, false);
        CircleImageView imageView = view.findViewById(R.id.iv_latelySearch);

        // Glide를 사용하여 이미지 로드
        Glide.with(this).load(imageUrl).into(imageView);

        return view;
    }
}
