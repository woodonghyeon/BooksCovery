package com.example.androidteamproject.Search;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidteamproject.ApiData.SearchBookTitle;
import com.example.androidteamproject.R;
import com.squareup.picasso.Picasso;

public class FragmentBookDetail extends Fragment {
    private static final String ARG_BOOK = "book";

    private SearchBookTitle book;

    public FragmentBookDetail() {
        // Required empty public constructor
    }

    public static FragmentBookDetail newInstance(SearchBookTitle book) {
        FragmentBookDetail fragment = new FragmentBookDetail();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (SearchBookTitle) getArguments().getSerializable(ARG_BOOK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        TextView tvBookName = view.findViewById(R.id.tv_detail_book_name);
        TextView tvAuthors = view.findViewById(R.id.tv_detail_authors);
        TextView tvPublisher = view.findViewById(R.id.tv_detail_authors);
        ImageView ivBookImage = view.findViewById(R.id.iv_detail_book_image);

        if (book != null) {
            tvBookName.setText(book.getBookName());
            tvAuthors.setText(book.getAuthors());

            String imageUrl = book.getBookImageUrl();
            if (imageUrl == null || imageUrl.isEmpty()) {
                Picasso.get().load(R.drawable.ic_warring).into(ivBookImage);
            } else {
                Picasso.get().load(imageUrl).into(ivBookImage);
            }
        }

        return view;
    }
}
