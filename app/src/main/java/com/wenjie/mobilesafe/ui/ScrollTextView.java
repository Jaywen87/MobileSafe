package com.wenjie.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class ScrollTextView extends androidx.appcompat.widget.AppCompatTextView {

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
