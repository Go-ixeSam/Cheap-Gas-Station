package com.hoanpham.uit.cheapgasstation.Base;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.hoanpham.uit.cheapgasstation.R;

public class BaseTextView extends AppCompatTextView {

    protected Typeface mRegularTypeface;
    protected Typeface mBoldTypeface;

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRegularTypeface = ResourcesCompat.getFont(getContext(), R.font.nunitosans_regular);
        mBoldTypeface = ResourcesCompat.getFont(getContext(), R.font.nunitosans_bold);
        setDefaultProperties();
    }

    protected void setDefaultProperties(){
        setTypeface(mRegularTypeface);
        setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }
}
