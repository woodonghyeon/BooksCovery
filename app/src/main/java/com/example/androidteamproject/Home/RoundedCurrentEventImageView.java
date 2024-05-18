package com.example.androidteamproject.Home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

// 현재 진행중인 이벤트 이미지의 코너를 둥글게 만들기 위함
public class RoundedCurrentEventImageView extends AppCompatImageView {

    private float radius = 30.0f; // 원하는 반지름 값 (dp 단위로 설정)

    public RoundedCurrentEventImageView(Context context) {
        super(context);
    }

    public RoundedCurrentEventImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedCurrentEventImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Clip the canvas to the rounded path
        Path path = new Path();
        path.addRoundRect(0, 0, this.getWidth(), this.getHeight(), radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }
}
