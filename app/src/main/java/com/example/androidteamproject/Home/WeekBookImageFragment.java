package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidteamproject.R;

public class WeekBookImageFragment extends Fragment {
    private static final String ARG_IMAGE_RES_ID = "imageResId";

    private int imageResId;

    public WeekBookImageFragment() {
        // Required empty public constructor
    }

    public static WeekBookImageFragment newInstance(int imageResId) {
        WeekBookImageFragment fragment = new WeekBookImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageResId = getArguments().getInt(ARG_IMAGE_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 이미지를 표시하기 위한 ImageView를 생성하고 이미지 리소스 ID를 설정하여 이미지를 표시
        View rootView = inflater.inflate(R.layout.week_book_img, container, false);
        ImageView imageView = rootView.findViewById(R.id.iv_weekBookImg);
        imageView.setImageResource(imageResId);
        return rootView;
    }
}
