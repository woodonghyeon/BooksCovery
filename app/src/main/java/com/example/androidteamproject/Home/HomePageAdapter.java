package com.example.androidteamproject.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomePageAdapter extends FragmentStateAdapter {
    private final int[] imageResIds;
    private final int imageCount;

    public HomePageAdapter(FragmentActivity fa, int count) {
        super(fa);
        imageCount = count;
        // 이미지 리소스 ID 배열 초기화
        imageResIds = new int[imageCount];
        // 이미지 리소스 ID 배열에 각 이미지에 해당하는 리소스 ID 저장
        for (int i = 0; i < imageCount; i++) {
            // 이미지 파일 이름이 순차적으로 설정되어 있다면, 리소스 ID를 계산하여 배열에 저장
            String imageName = "test" + (i + 1); // 예: test1, test2, test3
            imageResIds[i] = fa.getResources().getIdentifier(imageName, "drawable", fa.getPackageName());
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 이미지 리소스 ID 배열에서 현재 페이지에 해당하는 이미지의 리소스 ID를 가져와서 Fragment를 생성하여 반환
        int index = getRealPosition(position);
        return CurrentEventImageFragment.newInstance(imageResIds[index]);
    }

    @Override
    public int getItemCount() {
        // 페이지 수는 이미지의 개수와 동일하게 설정
        // 이미지 개수의 배수로 설정하여 무한 스크롤 가능하도록 함
        return imageCount * 1000; // 1000은 임의로 설정한 값
    }

    public int getRealPosition(int position) {
        // 무한 스크롤을 위해 실제 위치를 계산하여 반환
        return position % imageCount;
    }
}
