package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.androidteamproject.R;

public class CurrentEventImageFragment extends Fragment {
    private static final String ARG_IMAGE_RES_ID = "imageResId";
    private int imageResId;

    public CurrentEventImageFragment() {
        // Required empty public constructor
    }

    public static CurrentEventImageFragment newInstance(int imageResId) {
        CurrentEventImageFragment fragment = new CurrentEventImageFragment();
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
        View rootView = inflater.inflate(R.layout.current_event_img, container, false);
        RoundedCurrentEventImageView imageView = rootView.findViewById(R.id.iv_currentEvent);
        imageView.setImageResourceWithResize(getContext(), imageResId);
        return rootView;
    }
}
