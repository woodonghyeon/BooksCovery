package com.example.androidteamproject.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class PopularBooksAdapter extends FragmentStateAdapter {
    private List<String> bookName;
    private List<String> authors;
    private List<String> imageUrl;
    private List<String> isbn13;
    private OnItemClickListener onItemClickListener;
    private static final int MAX_VALUE = 2000; // 충분히 큰 값

    public PopularBooksAdapter(FragmentActivity fa, List<String> bookName, List<String> authors, List<String> imageUrl, List<String> isbn13, OnItemClickListener onItemClickListener) {
        super(fa);
        this.bookName = bookName;
        this.authors = authors;
        this.imageUrl = imageUrl;
        this.isbn13 = isbn13;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = position % imageUrl.size(); // 인덱스를 이미지 URL의 개수로 나눈 나머지로 계산
        return PopularBookFragment.newInstance(bookName.get(index), authors.get(index), imageUrl.get(index), isbn13.get(index), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return MAX_VALUE;
    }

    public interface OnItemClickListener {
        void onItemClick(String bookName, String authors, String imageUrl, String isbn13);
    }
}

