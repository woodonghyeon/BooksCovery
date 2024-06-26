package com.example.androidteamproject.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ManiaBookAdapter extends FragmentStateAdapter {
    private final List<String> imageUrls;
    private final List<String> bookNames;
    private final List<String> isbn13s;
    private static final int MAX_VALUE = 2000; // 충분히 큰 값

    public ManiaBookAdapter(FragmentActivity fa, List<String> bookNames, List<String> imageUrls, List<String> isbn13s) {
        super(fa);
        this.imageUrls = imageUrls;
        this.bookNames = bookNames;
        this.isbn13s = isbn13s;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 이미지 리소스 대신 URL을 전달하여 Fragment를 생성
        int index = position % imageUrls.size(); // 인덱스를 이미지 URL의 개수로 나눈 나머지로 계산
        return ManiaBookImageFragment.newInstance(bookNames.get(index), imageUrls.get(index), isbn13s.get(index));
    }

    @Override
    public int getItemCount() {
        // 페이지 수는 이미지 URL의 개수와 동일하게 설정
        return MAX_VALUE;
    }

    public interface OnItemClickListener {
        void onItemClick(String isbn13, String bookName, String imageUrl);
    }
}

