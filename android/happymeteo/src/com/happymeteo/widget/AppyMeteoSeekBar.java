package com.happymeteo.widget;

import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;

public class AppyMeteoSeekBar extends SeekBar {
	private TextView labelBackground;
	private int viewWidth;
	private int barHeight;
	
	public AppyMeteoSeekBar(Context context) {
		super(context);
	}
	
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
     {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (labelBackground != null)
        {
        	viewWidth = getMeasuredWidth();
            barHeight = getMeasuredHeight();// returns only the bar height (without the label);
            setMeasuredDimension(viewWidth, barHeight + labelBackground.getHeight());
        }

    }

    /*@Override
    protected synchronized void onDraw(Canvas canvas)
    {
        if (labelBackground != null)
        {
            barBounds.left = getPaddingLeft();
            barBounds.top = labelBackground.getHeight() + getPaddingTop();
            barBounds.right = barBounds.left + viewWidth - getPaddingRight() - getPaddingLeft();
            barBounds.bottom = barBounds.top + barHeight - getPaddingBottom() - getPaddingTop();

            progressPosX = barBounds.left + ((float) this.getProgress() / (float) this.getMax()) * barBounds.width();

            labelPos.x = (int) progressPosX - labelOffset;
            labelPos.y = getPaddingTop();

            progressDrawable = getProgressDrawable();
            progressDrawable.setBounds(barBounds.left, barBounds.top, barBounds.right, barBounds.bottom);
            progressDrawable.draw(canvas);

            labelTextPaint.getTextBounds(labelText, 0, labelText.length(), labelTextRect);

            canvas.drawBitmap(labelBackground, labelPos.x, labelPos.y, labelBackgroundPaint);
            canvas.drawText(labelText, labelPos.x + labelBackground.getWidth() / 2 - labelTextRect.width() / 2, labelPos.y + labelBackground.getHeight() / 2 + labelTextRect.height() / 2, labelTextPaint);

            thumbX = (int) progressPosX - getThumbOffset();
            thumbDrawable.setBounds(thumbX, barBounds.top, thumbX + thumbDrawable.getIntrinsicWidth(), barBounds.top + thumbDrawable.getIntrinsicHeight());
            thumbDrawable.draw(canvas);
        } else
        {
            super.onDraw(canvas);
        }
    }*/

}
