package com.example.androidteamproject.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidteamproject.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class CurrentEventImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private String imageUrl;

    public CurrentEventImageFragment() {
        // Required empty public constructor
    }

    public static CurrentEventImageFragment newInstance(String imageUrl) {
        CurrentEventImageFragment fragment = new CurrentEventImageFragment();
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
        View rootView = inflater.inflate(R.layout.current_event_img, container, false);
        ImageView imageView = rootView.findViewById(R.id.iv_currentEvent);

        // 로그 추가
        Log.d("CurrentEventImageFragment", "Loading image URL: " + imageUrl);

        // HTTP 로깅 인터셉터 추가
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient 설정
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    return chain.proceed(chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .build());
                })
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Picasso picasso = new Picasso.Builder(getContext())
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();

        picasso.load(imageUrl)
                .error(R.drawable.ic_error)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE) // 캐시 사용 안함
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("CurrentEventImageFragment", "Image loaded successfully.");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("CurrentEventImageFragment", "Failed to load image: " + e.getMessage());
                    }
                });

        return rootView;
    }
}
