package com.ctao.dialer.widght;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ctao.dialer.R;

/**
 * Created by A Miracle on 2016/4/27.
 */
public class DigitsEditText extends EditText{

	private final int mOriginalTextSize;
    private final int mMinTextSize;

    public DigitsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOriginalTextSize = (int) getTextSize();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DigitsEditText);
        mMinTextSize = (int) a.getDimension(R.styleable.DigitsEditText_resizing_text_min_size,
                mOriginalTextSize);
        a.recycle();
        
        
        setInputType(getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        //setShowSoftInputOnFocus(false);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        resizeText(this, mOriginalTextSize, mMinTextSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resizeText(this, mOriginalTextSize, mMinTextSize);
    }
    
    public static void resizeText(TextView textView, int originalTextSize, int minTextSize) {
        final Paint paint = textView.getPaint();
        final int width = textView.getWidth();
        if (width == 0) return;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalTextSize);
        float ratio = width / paint.measureText(textView.getText().toString());
        if (ratio <= 1.0f) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    Math.max(minTextSize, originalTextSize * ratio));
        }
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        final InputMethodManager imm = ((InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        if (imm != null && imm.isActive(this)) {
            imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean ret = super.onTouchEvent(event);
        // Must be done after super.onTouchEvent()
        final InputMethodManager imm = ((InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        if (imm != null && imm.isActive(this)) {
            imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
        }
        return ret;
    }
}

