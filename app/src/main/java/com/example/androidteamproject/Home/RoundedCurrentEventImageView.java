package com.example.androidteamproject.Home;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

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
        Path path = new Path();
        path.addRoundRect(0, 0, this.getWidth(), this.getHeight(), radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    public void loadImage(Context context, String url) {
        // View의 크기를 얻어오기 위해 post를 사용
        this.post(() -> {
            int width = this.getWidth();
            int height = this.getHeight();

            // View의 크기를 알 수 없으면 다시 시도
            if (width == 0 || height == 0) {
                return;
            }

            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .override(width, height) // View의 크기로 비트맵 크기 조정
                            .fitCenter()) // 이미지 비율 유지하며 맞춤
                    .into(this);
        });
    }

    public void setImageResourceWithResize(Context context, int resId) {
        // View의 크기를 얻어오기 위해 post를 사용
        this.post(() -> {
            int width = this.getWidth();
            int height = this.getHeight();

            // View의 크기를 알 수 없으면 다시 시도
            if (width == 0 || height == 0) {
                return;
            }

            Glide.with(context)
                    .load(resId)
                    .apply(new RequestOptions()
                            .override(width, height) // View의 크기로 비트맵 크기 조정
                            .fitCenter()) // 이미지 비율 유지하며 맞춤
                    .into(this);
        });
    }
}
