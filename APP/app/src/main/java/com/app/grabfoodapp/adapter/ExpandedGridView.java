package com.app.grabfoodapp.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ExpandedGridView extends GridView {

    public ExpandedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedGridView(Context context) {
        super(context);
    }

    public ExpandedGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Đo chiều cao cực lớn để GridView tự co hết item
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST
        );
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

