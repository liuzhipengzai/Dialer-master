package com.ctao.dialer.widght;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctao.dialer.R;

/**
 * Created by A Miracle on 2016/4/27.
 */
public class DialerKeyView extends RelativeLayout {

	private TextView tv_text1;
	private TextView tv_text2;
	private String text1;
	private String text2;

	public DialerKeyView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	public DialerKeyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public DialerKeyView(Context context) {
		super(context);
		init(null);
	}

	private void init(AttributeSet attrs) {
		View.inflate(getContext(), R.layout.view_dialer_key, this);
		tv_text1 = (TextView) findViewById(R.id.tv_text1);
		tv_text2 = (TextView) findViewById(R.id.tv_text2);
		
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DialerKeyView);
			text1 = a.getString(R.styleable.DialerKeyView_text1);
			text2 = a.getString(R.styleable.DialerKeyView_text2);
			a.recycle();
		}
		
		if(text1 != null){
			tv_text1.setText(text1);
		}
		
		if(text2 != null){
			tv_text2.setText(text2);
		}
	}
}

