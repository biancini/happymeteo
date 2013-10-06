package com.happymeteo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.SeekBar;

public class AppyMeteoSeekBar extends SeekBar {
	private int viewWidth;
	private Rect barBounds;
	private int progressPosX;
	
	public interface OnAppyMeteoSeekBarChangeListener {
		void onProgressPosXChanged(AppyMeteoSeekBar seekBar, int progress, int progressPosX);
	}
	
	 private OnAppyMeteoSeekBarChangeListener mOnAppyMeteoSeekBarChangeListener;
	
	public AppyMeteoSeekBar(Context context) {
		super(context);
		
		barBounds = new Rect();
	}
	
	public void setOnAppyMeteoSeekBarChangeListener(OnAppyMeteoSeekBarChangeListener l) {
        mOnAppyMeteoSeekBarChangeListener = l;
    }
	
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
     {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas)
    {
        barBounds.left = getPaddingLeft();
        barBounds.right = barBounds.left + viewWidth - getPaddingRight() - getPaddingLeft();

        progressPosX = (int) (barBounds.left + ((float) this.getProgress() / (float) this.getMax()) * barBounds.width() + getThumbOffset()/2);
        
        if (mOnAppyMeteoSeekBarChangeListener != null) {
        	mOnAppyMeteoSeekBarChangeListener.onProgressPosXChanged(this, getProgress(), progressPosX);
        }
        
        super.onDraw(canvas);
    }
    
    public int getProgressPosX() {
    	return progressPosX;
    }
}
