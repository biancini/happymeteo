package com.happymeteo.meteo;

import com.happymeteo.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

public class GraphViewContentView extends View {
	private GraphViewStyle graphViewStyle = new GraphViewStyle(0xff000000, 0xffffffff, 0xffffffff);
	private final Paint paint = new Paint();
	
	private float scale = 1f;
	
	private float[] graphData = null;
	private String[] horlabels = null;
	private final Rect textBounds = new Rect();
	
	@SuppressWarnings("deprecation")
	public GraphViewContentView(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		paint.setAntiAlias(true);
		paint.setStrokeWidth(0);

		float border = graphViewStyle.getBorderSize() * scale;
		
		// measure bottom text
	
		paint.setTextSize(graphViewStyle.getTextSize() * scale);
		String testLabel = horlabels[0];
		paint.getTextBounds(testLabel, 0, testLabel.length(), textBounds);
		float horLabelTextHeight = (textBounds.height());
		border += horLabelTextHeight;

		paint.setStrokeCap(Paint.Cap.ROUND);

		// horizontal labels + lines
		int hors = horlabels.length - 1;
		if (hors < 1) hors = 1;

		int numValues = 0;
		for (int i = 0; i < graphData.length; i++) {
			drawSeries(canvas, graphData, border, (float) 0, (float) 0, graphData.length-1, (float) 10, (float) 0);
			if (graphData.length > numValues) numValues = graphData.length;
		}

		drawHorizontalLabels(canvas, horLabelTextHeight, numValues, border, (float) 0);
	}
	
	public Paint getPaint() {
		return paint;
	}

	public void setGraphData(float[] graphData) {
		this.graphData = graphData;
	}
	
	public void drawSeries(Canvas canvas, float[] values,
			float border, float minX, float minY,
			float diffX, float diffY, float horstart) {
		
		float graphheight = (float) getHeight() - (2 * border);
		float graphwidth = (float) (getWidth() - 1);
		
		paint.setStrokeWidth(3.0f * scale);

		// draw background
		float lastEndY = minY / diffY * graphwidth;
		float lastEndX = minX / diffX * graphwidth;

		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.baloon);
		
		float[] valY = new float[values.length];
		float[] endX = new float[values.length];
		float[] endY = new float[values.length];
		
		for (int i = 0; i < values.length; i++) {
			valY[i] = values[i] - minY;
			float y = graphheight * valY[i] / diffY;

			float valX = i - minX;
			float x = graphwidth * valX / diffX;
			
			endX[i] = (float) x + (horstart + 1);
			endY[i] = (float) (border - y) + graphheight + 2;
		}
		
		lastEndY = endY[0];
		lastEndX = endX[0];
		
		paint.setColor(getResources().getColor(R.color.yellow));
		for (int i = 1; i < values.length; i++) {
			// fill space between last and current point
			float numSpace = ((endX[i] - lastEndX) / 3f) + 1;
			for (int xi = 0; xi < numSpace; xi++) {
				float spaceX = (float) (lastEndX + ((endX[i] - lastEndX) * xi / (numSpace - 1)));
				float spaceY = (float) (lastEndY + ((endY[i] - lastEndY) * xi / (numSpace - 1)));
				float startX = spaceX;

				if (startX - horstart > 1) canvas.drawLine(startX, graphheight + border, spaceX, spaceY, paint);
			}

			lastEndY = endY[i];
			lastEndX = endX[i];
		}
		
		float spaceFromPoint = 5;
		paint.setColor(getResources().getColor(R.color.white));
		for (int i = 0; i < values.length; i++) {			
//			if (i ==  values.length-1) {
//				canvas.drawBitmap(mirroredBitmap.copy(Bitmap.Config.ARGB_8888, true), endX[i] - bitmap.getWidth(), endY[i] - bitmap.getHeight() - spaceFromPoint, paint);
//				canvas.drawText(String.valueOf((int) valY[i]), endX[i] - bitmap.getWidth() + bitmap.getWidth()/3 + spaceFromPoint, endY[i] - bitmap.getHeight()/2 - spaceFromPoint, paint);
//			}
			if (i < values.length-1) {
				canvas.drawBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true), endX[i], endY[i] - bitmap.getHeight() - spaceFromPoint, paint);
				canvas.drawText(String.valueOf((int) valY[i]), endX[i] + bitmap.getWidth()/3 + spaceFromPoint, endY[i] - bitmap.getHeight()/2 - spaceFromPoint, paint);
			}
		}

	}

	protected void drawHorizontalLabels(Canvas canvas, float horLabelTextHeight, int weekNum, float border, float horstart) {
		float graphheight = (float) getHeight() - (2 * border);
		float graphwidth = (float) (getWidth() - 1);
		
		paint.setTextSize(graphViewStyle.getTextSize() * scale);
		paint.setTextAlign(Align.LEFT);

		int hors = horlabels.length - 1;
		if (hors < 1) hors = 1;

		paint.setColor(getResources().getColor(R.color.black));
		paint.setStyle(Style.FILL);
		
		// left, top, right, bottom
		canvas.drawRect(0, graphheight + border, graphwidth, graphheight + (2 * border), paint);

		paint.setTextAlign(Align.CENTER);
		paint.setColor(getResources().getColor(R.color.white));

		for (int i = 0; i < weekNum; ++i){
			float x_text = ((graphwidth / (weekNum * 2)) * ((i * 2) + 1)) + horstart;
			float y_text = graphheight + border + (border / 2) - 2 * scale;
			canvas.drawText("Week " + (i+1), x_text, y_text, paint);
		}
		
		for (int i = 0; i < horlabels.length; ++i) {
			float x_text = ((graphwidth / (horlabels.length * 2)) * ((i * 2) + 1)) + horstart;
			float y_text = graphheight + border + (border / 2) + horLabelTextHeight;
			canvas.drawText(horlabels[i], x_text, y_text, paint);
		}
	}

	public void setHorlabels(String[] horlabels) {
		this.horlabels = horlabels;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void setGraphViewStyle(GraphViewStyle style) {
		graphViewStyle = style;
	}

}
