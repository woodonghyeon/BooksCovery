package com.example.androidteamproject.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class CurrentEventAdapter extends FragmentStateAdapter {
    private final List<String> imageUrls;

    public CurrentEventAdapter(FragmentActivity fa, List<String> imageUrls) {
        super(fa);
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 이미지 URL 배열에서 현재 페이지에 해당하는 URL을 가져와서 Fragment를 생성하여 반환
        int index = getRealPosition(position);
        return CurrentEventImageFragment.newInstance(imageUrls.get(index));
    }

    @Override
    public int getItemCount() {
        // 페이지 수는 이미지 URL의 개수와 동일하게 설정
        // 이미지 개수의 배수로 설정하여 무한 스크롤 가능하도록 함
        return imageUrls.size() * 1000; // 1000은 임의로 설정한 값
    }

    public int getRealPosition(int position) {
        // 무한 스크롤을 위해 실제 위치를 계산하여 반환
        return position % imageUrls.size();
    }
}
