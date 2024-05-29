package com.example.androidteamproject.Home;

import android.content.Context;
import android.widget.TextView;

import com.example.androidteamproject.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

public class CustomMarkerView extends MarkerView {
    private final TextView tvContent;
    private final List<String> month;

    public CustomMarkerView(Context context, int layoutResource, List<String> month) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
        this.month = month;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(String.format("%s\n 대출 건수: %.0f권", month.get((int) e.getX()), e.getY()));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        MPPointF offset = getOffset();
        Chart chart = getChartView();

        float width = getWidth();
        float height = getHeight();

        float newX = posX + offset.x;
        float newY = posY + offset.y;

        if (chart != null) {
            // X축 경계 조정
            if (newX < 0) {
                offset.x = -posX;
            } else if (newX + width > chart.getWidth()) {
                offset.x = chart.getWidth() - posX - width;
            }

            // Y축 경계 조정
            if (newY < 0) {
                offset.y = -posY;
            } else if (newY + height > chart.getHeight()) {
                offset.y = chart.getHeight() - posY - height;
            }
        }

        return offset;
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
