package com.example.androidteamproject.Home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.androidteamproject.Search.LatelySearchImageFragment;

import java.util.ArrayList;
import java.util.List;

public class WeekBookAdapter extends FragmentStateAdapter {
    private final List<String> weekBookName;
    private final List<String> weekBookImg;

    public WeekBookAdapter(FragmentActivity fa, List<String> weekBookName, List<String> weekBookNmg) {
        super(fa);
        this.weekBookName = weekBookName;
        this.weekBookImg = weekBookNmg;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 이미지 리소스 ID 배열에서 현재 페이지에 해당하는 이미지의 리소스 ID를 가져와서 Fragment를 생성하여 반환
        int index = position % weekBookImg.size(); // 인덱스를 이미지 URL의 개수로 나눈 나머지로 계산
        return LatelySearchImageFragment.newInstance(weekBookName.get(index), weekBookImg.get(index));
    }

    @Override
    public int getItemCount() {
        // 페이지 수는 이미지의 개수와 동일하게 설정
        // 이미지 개수의 배수로 설정하여 무한 스크롤 가능하도록 함
        return weekBookImg.size(); // 1000은 임의로 설정한 값
    }

    /*
    public int getRealPosition(int position) {
        // 무한 스크롤을 위해 실제 위치를 계산하여 반환
        return position % 1;
    }
     */
}
