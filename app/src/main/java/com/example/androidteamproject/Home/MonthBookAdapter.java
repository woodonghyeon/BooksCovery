package com.example.androidteamproject.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.androidteamproject.Search.LatelySearchImageFragment;

import java.time.Month;
import java.util.List;

public class MonthBookAdapter extends FragmentStateAdapter {
    private final List<String> imageUrls;
    private final List<String> bookName;
    private final List<String> authors;
    private static final int MAX_VALUE = 2000; // 충분히 큰 값

    public MonthBookAdapter(FragmentActivity fa, List<String> bookName, List<String> authors, List<String> imageUrls) {
        super(fa);
        this.imageUrls = imageUrls;
        this.bookName = bookName;
        this.authors = authors;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 이미지 리소스 대신 URL을 전달하여 Fragment를 생성
        int index = position % imageUrls.size(); // 인덱스를 이미지 URL의 개수로 나눈 나머지로 계산
        return MonthBookImageFragment.newInstance(bookName.get(index), authors.get(index), imageUrls.get(index));
    }

    @Override
    public int getItemCount() {
        // 페이지 수는 이미지 URL의 개수와 동일하게 설정
        return MAX_VALUE;
    }
}

