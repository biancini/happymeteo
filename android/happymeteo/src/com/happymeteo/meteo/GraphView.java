package com.happymeteo.meteo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.happymeteo.R;

public class GraphView extends LinearLayout {
	private String[] horlabels = null;
	private Integer horLabelTextHeight = null;
	private Integer horLabelTextWidth = null;
	private GraphViewStyle graphViewStyle = new GraphViewStyle(0xff000000, 0xffffffff, 0xffffffff);
	
	private final Paint paint = new Paint();
	private final Rect textBounds = new Rect();
	private final GraphViewContentView graphViewContentView;
	private final List<GraphViewSeries> graphSeries;
	
	static private final float BORDER = 20;

	private class GraphViewContentView extends View {
		/**
		 * @param context
		 */
		@SuppressWarnings("deprecation")
		public GraphViewContentView(Context context) {
			super(context);
			setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}

		/**
		 * @param canvas
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			paint.setAntiAlias(true);
			paint.setStrokeWidth(0);

			float border = BORDER;
			
			// measure bottom text
			if (horLabelTextHeight == null || horLabelTextWidth == null) {
				paint.setTextSize(graphViewStyle.getTextSize());
				String testLabel = horlabels[0];
				paint.getTextBounds(testLabel, 0, testLabel.length(), textBounds);
				horLabelTextHeight = (textBounds.height());
				horLabelTextWidth = (textBounds.width());
			}
			border += horLabelTextHeight;

			paint.setStrokeCap(Paint.Cap.ROUND);

			paint.setTextSize(20f);
			paint.setTextAlign(Align.LEFT);
			// horizontal labels + lines
			int hors = horlabels.length - 1;
			if (hors < 1) hors = 1;

			int numValues = 0;
			for (int i = 0; i < graphSeries.size(); i++) {
				drawSeries(canvas, graphSeries.get(i).values, border, (float) 0, (float) 0, getMaxX(), (float) 10, (float) 0);
				if (graphSeries.get(i).values.length > numValues) numValues = graphSeries.get(i).values.length;
			}

			drawHorizontalLabels(canvas, horlabels, numValues, border, (float) 0);
		}
	}

	public GraphView(Context context, AttributeSet attrs) {
		this(context);

		int width = attrs.getAttributeIntValue("android", "layout_width", LayoutParams.MATCH_PARENT);
		int height = attrs.getAttributeIntValue("android", "layout_height", LayoutParams.MATCH_PARENT);
		
		setLayoutParams(new LayoutParams(width, height));
	}

	/**
	 * @param context
	 * @param title
	 *            [optional]
	 */
	@SuppressWarnings("deprecation")
	public GraphView(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		graphSeries = new ArrayList<GraphViewSeries>();
		graphViewContentView = new GraphViewContentView(context);
		addView(graphViewContentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
	}

	/**
	 * add a series of data to the graph
	 * 
	 * @param series
	 */
	public void addSeries(GraphViewSeries series) {
		series.addGraphView(this);
		graphSeries.add(series);
		redrawAll();
	}

	/**
	 * @param canvas
	 * @param values
	 * @param graphwidth
	 * @param graphheight
	 * @param border
	 * @param minX
	 * @param minY
	 * @param diffX
	 * @param diffY
	 * @param horstart
	 */
	public void drawSeries(Canvas canvas, GraphViewData[] values,
			float border, float minX, float minY,
			float diffX, float diffY, float horstart) {
		
		float graphheight = (float) getHeight() - (2 * border);
		float graphwidth = (float) (getWidth() - 1);
		
		paint.setStrokeWidth(3.0f);

		// draw background
		float lastEndY = minY / diffY * graphwidth;
		float lastEndX = minX / diffX * graphwidth;

		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.baloon);
		
		float[] valY = new float[values.length];
		float[] endX = new float[values.length];
		float[] endY = new float[values.length];
		
		for (int i = 0; i < values.length; i++) {
			valY[i] = values[i].getY() - minY;
			float y = graphheight * valY[i] / diffY;

			float valX = values[i].getX() - minX;
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

	protected void drawHorizontalLabels(Canvas canvas, String[] horlabels, int weekNum, float border, float horstart) {
		float graphheight = (float) getHeight() - (2 * border);
		float graphwidth = (float) (getWidth() - 1);
		
		paint.setTextSize(20f);
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
			canvas.drawText("Week " + (i+1), x_text, graphheight + border + (border / 2) - 3, paint);
		}
		
		for (int i = 0; i < horlabels.length; ++i) {
			float x_text = ((graphwidth / (horlabels.length * 2)) * ((i * 2) + 1)) + horstart;
			canvas.drawText(horlabels[i], x_text, graphheight + border + (border / 2) + horLabelTextHeight - 5, paint);
		}
	}

	/**
	 * returns the maximal X value of the current viewport (if viewport is set)
	 * otherwise maximal X value of all data.
	 */
	protected float getMaxX() {
		float highest = 0;
		if (graphSeries.size() > 0) {
			GraphViewData[] values = graphSeries.get(0).values;
			highest = (values.length == 0) ? 0 : values[values.length - 1].getX();

			for (int i = 1; i < graphSeries.size(); i++) {
				values = graphSeries.get(i).values;

				if (values.length > 0) {
					highest = Math.max(highest, values[values.length - 1].getX());
				}
			}
		}
		return highest;
	}

	/**
	 * forces graphview to invalide all views and caches. Normally there is no
	 * need to call this manually.
	 */
	public void redrawAll() {
		horLabelTextHeight = null;
		horLabelTextWidth = null;

		invalidate();
		graphViewContentView.invalidate();
	}

	/**
	 * removes all series
	 */
	public void removeAllSeries() {
		for (GraphViewSeries s : graphSeries) {
			s.removeGraphView(this);
		}
		while (!graphSeries.isEmpty()) {
			graphSeries.remove(0);
		}
		redrawAll();
	}

	/**
	 * removes a series
	 * 
	 * @param series
	 *            series to remove
	 */
	public void removeSeries(GraphViewSeries series) {
		series.removeGraphView(this);
		graphSeries.remove(series);
		redrawAll();
	}

	/**
	 * removes series
	 * 
	 * @param index
	 */
	public void removeSeries(int index) {
		if (index < 0 || index >= graphSeries.size()) {
			throw new IndexOutOfBoundsException("No series at index " + index);
		}

		removeSeries(graphSeries.get(index));
	}

	/**
	 * set custom graphview style
	 * 
	 * @param style
	 */
	public void setGraphViewStyle(GraphViewStyle style) {
		graphViewStyle = style;
		horLabelTextHeight = null;
	}

	/**
	 * set's static horizontal labels (from left to right)
	 * 
	 * @param horlabels
	 *            if null, labels were generated automatically
	 */
	public void setHorizontalLabels(String[] horlabels) {
		this.horlabels = horlabels;
	}
}
