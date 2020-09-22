package com.hoanpham.uit.cheapgasstation.Views.TextView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.hoanpham.uit.cheapgasstation.Base.BaseTextView;
import com.hoanpham.uit.cheapgasstation.R;

public class RegularBlackTextView extends BaseTextView {

    public RegularBlackTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void setDefaultProperties() {
        super.setDefaultProperties();
        setTypeface(mRegularTypeface);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }
}
