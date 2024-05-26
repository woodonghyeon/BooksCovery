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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class HotTrendBookImageFragment extends Fragment {
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
    private HotTrendBookAdapter.OnItemClickListener onItemClickListener;

    public static HotTrendBookImageFragment newInstance(String class_nm, String bookName, String authors, String imageUrl, String isbn13, HotTrendBookAdapter.OnItemClickListener onItemClickListener) {
        HotTrendBookImageFragment fragment = new HotTrendBookImageFragment();
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
        View view = inflater.inflate(R.layout.hottrend_book_img, container, false);
        ImageView hotTrendBookImg = view.findViewById(R.id.iv_hotTrendBookImg);
        TextView hotTrendBookTitle = view.findViewById(R.id.tv_hotTrendTitle);
        TextView hotTrendBookAuthor = view.findViewById(R.id.tv_hotTrendBookAuthor);
        TextView hotTrendClassNm = view.findViewById(R.id.tv_hotTrendClassName);

        if (getArguments() != null) {
            class_nm = getArguments().getString(ARG_CLASS_NM);
            bookName = getArguments().getString(ARG_BOOKNAME);
            authors = getArguments().getString(ARG_AUTHORS);
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            isbn13 = getArguments().getString(ARG_ISBN13);
        }

        if (imageUrl == null || imageUrl.isEmpty()) {
            hotTrendBookImg.setImageResource(R.drawable.ic_error); // 기본 이미지 설정
        } else {
            Picasso.get().load(imageUrl)
                    .error(R.drawable.ic_error) // 로드 실패 시 기본 이미지 설정
                    .into(hotTrendBookImg, new Callback() {
                        @Override
                        public void onSuccess() {
                            System.out.println("Picasso: Image loaded successfully.");
                        }

                        @Override
                        public void onError(Exception e) {
                            System.err.println("Picasso: Failed to load image. " + e.getMessage());
                        }
                    });
        }

        hotTrendBookImg.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(isbn13, bookName, authors, imageUrl);
            }
        });

        hotTrendBookTitle.setText(bookName);
        hotTrendBookAuthor.setText(authors);
        hotTrendClassNm.setText(class_nm);

        return view;
    }

    public void setOnItemClickListener(HotTrendBookAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
