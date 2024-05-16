package com.example.androidteamproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CurrentEventFragment extends Fragment {
    private static final String ARG_IMAGE_RES_ID = "imageResId";

    private int imageResId;

    public CurrentEventFragment() {
        // Required empty public constructor
    }

    public static CurrentEventFragment newInstance(int imageResId) {
        CurrentEventFragment fragment = new CurrentEventFragment();
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
        View rootView = inflater.inflate(R.layout.current_event, container, false);
        ImageView imageView = rootView.findViewById(R.id.iv_currentImg);
        imageView.setImageResource(imageResId);
        return rootView;
    }
}
