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
import com.example.androidteamproject.Home.MonthBookAdapter;
import com.example.androidteamproject.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class LatelySearchImageFragment extends Fragment {
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_BOOKNAME = "bookName";
    private static final String ARG_AUTHORS = "authors";
    private static final String ARG_ISBN13 = "isbn13";
    private String imageUrl;
    private String bookName;
    private String authors;
    private String isbn13;
    private SearchPageAdapter.OnItemClickListener onItemClickListener;

    public static LatelySearchImageFragment newInstance(String bookName, String imageUrl, String authors, String isbn13, SearchPageAdapter.OnItemClickListener onItemClickListener) {
        LatelySearchImageFragment fragment = new LatelySearchImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_BOOKNAME, bookName);
        args.putString(ARG_AUTHORS, authors);
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
            isbn13 = getArguments().getString(ARG_ISBN13);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lately_search_book_img, container, false);
        CircleImageView imageView = view.findViewById(R.id.iv_latelySearch);
        TextView textView = view.findViewById(R.id.tv_latelySearch);

        Picasso.get().load(imageUrl).into(imageView);

        imageView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(isbn13, bookName, authors, imageUrl);
            }
        });

        textView.setText(bookName);

        return view;
    }

    public void setOnItemClickListener(SearchPageAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
