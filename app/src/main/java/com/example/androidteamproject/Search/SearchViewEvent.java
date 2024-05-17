package com.example.androidteamproject.Search;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.androidteamproject.R;

public class SearchViewEvent extends AppCompatActivity {
    SearchView sv_title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchview);

        sv_title = (SearchView) findViewById(R.id.sv_title);

        sv_title.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 텍스트 입력 후 전송시 이벤트 호출
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 텍스트 입력할 때 마다 이벤트 호출
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }
}
