package com.happymeteo.widget;

import android.content.Context;
import android.util.AttributeSet;

public class AppyMeteoTextView extends android.widget.TextView {
	private int maxlines;

	public AppyMeteoTextView(Context context) {
		super(context);
	}

	public AppyMeteoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AppyMeteoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setMaxLines(int maxlines) {
		this.maxlines = maxlines;
		
		super.setMaxLines(maxlines);
		super.invalidate();
	}
	
	@Override
	public int getMaxLines() {
		return maxlines;
	}
}