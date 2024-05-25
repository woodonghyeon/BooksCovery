package com.example.androidteamproject.Home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.androidteamproject.Home.FragmentBookDetail;
import com.example.androidteamproject.R;

public class BookDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ISBN13 = "isbn13";
    public static final String EXTRA_BOOKNAME = "bookName";
    public static final String EXTRA_AUTHORS = "authors";
    public static final String EXTRA_IMAGE_URL = "imageUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_book_detail);

        if (savedInstanceState == null) {
            String isbn13 = getIntent().getStringExtra(EXTRA_ISBN13);
            String bookName = getIntent().getStringExtra(EXTRA_BOOKNAME);
            String authors = getIntent().getStringExtra(EXTRA_AUTHORS);
            String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);

            FragmentBookDetail fragment = FragmentBookDetail.newInstance(isbn13, bookName, authors, imageUrl);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.ly_detail_book, fragment);
            transaction.commit();
        }
    }
}
