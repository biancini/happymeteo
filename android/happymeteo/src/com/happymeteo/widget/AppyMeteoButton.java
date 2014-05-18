package com.happymeteo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.Button;

import com.happymeteo.R;

public class AppyMeteoButton extends Button {
	private String text = null;
	private float textSize = 20f;
	private float scale = 1f;
	
	private Paint paint = new Paint();
	private Rect textBounds = new Rect();
	private Rect viewRect = null;
	private Bitmap background = null;
	private Context context;
	
	public AppyMeteoButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);

		this.context = context;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		scale = metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
	}
	
	public void init(AttributeSet attrs) { 
	    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AppyMeteoButton);
	    boolean dark = a.getBoolean(R.styleable.AppyMeteoButton_dark, false); 
	    text = a.getString(R.styleable.AppyMeteoButton_android_contentDescription);
		
	    paint.setAntiAlias(true);
	    paint.setStyle(Style.FILL);
	    paint.setTextAlign(Align.CENTER);
	    paint.setTextSize(textSize * scale);
		
		if (dark) {
			background = BitmapFactory.decodeResource(getResources(), R.drawable.button_dark);
			paint.setColor(getResources().getColor(R.color.white));
		} else {
			background = BitmapFactory.decodeResource(getResources(), R.drawable.button_light);
			paint.setColor(getResources().getColor(R.color.black));
		}
		
		paint.getTextBounds(text, 0, text.length(), textBounds);
		
	    //Don't forget this
	    a.recycle();
	}
	
    @Override
    protected synchronized void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
    	canvas.drawBitmap(background, null, viewRect, paint);
    	
    	Typeface fontType = Typeface.createFromAsset(context.getAssets(), "helveticaneueltstd-bd-webfont.ttf");
    	paint.setTypeface(fontType);
    	
    	int height = getMeasuredHeight();
    	canvas.drawText(text, height + ((getMeasuredWidth() - height) / 2), (height + textBounds.height()) / 2, paint);
    }
    
	@Override
	@SuppressLint("DrawAllocation")
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
    
}
