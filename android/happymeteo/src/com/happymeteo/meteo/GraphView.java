package com.happymeteo.meteo;

import java.text.NumberFormat;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.happymeteo.R;

public class GraphView extends LinearLayout {
	static final private class GraphViewConfig {
		static final float BORDER = 20;
	}

	private class GraphViewContentView extends View {
		private float lastTouchEventX;
		protected float graphwidth;
		private boolean scrollingStarted;

		/**
		 * @param context
		 */
		@SuppressWarnings("deprecation")
		public GraphViewContentView(Context context) {
			super(context);
			setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		}

		/**
		 * @param canvas
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			paint.setAntiAlias(true);
			paint.setStrokeWidth(0);

			float border = GraphViewConfig.BORDER;
			float horstart = 0;
			float height = getHeight();
			float width = getWidth() - 1;
			double maxY = getMaxY();
			double minY = getMinY();
			double maxX = getMaxX(false);
			double minX = getMinX(false);
			double diffX = maxX - minX;

			// measure bottom text
			if (labelTextHeight == null || horLabelTextWidth == null) {
				paint.setTextSize(getGraphViewStyle().getTextSize());
				String testLabel = horlabels[0];
				paint.getTextBounds(testLabel, 0, testLabel.length(),
						textBounds);
				labelTextHeight = (textBounds.height());
				horLabelTextWidth = (textBounds.width());
			}
			border += labelTextHeight;

			float graphheight = height - (2 * border);
			graphwidth = width;

			double diffY = maxY - minY;
			paint.setStrokeCap(Paint.Cap.ROUND);

			paint.setTextSize(20f);
			paint.setTextAlign(Align.LEFT);
			// horizontal labels + lines
			int hors = horlabels.length - 1;
			if (hors < 1)
				hors = 1;

			Log.i("GraphView", "graphwidth: " + graphwidth);
			Log.i("GraphView", "graphheight: " + graphheight);
			Log.i("GraphView", "horlabels.length: " + horlabels.length);

			if (maxY == minY) {
				// if min/max is the same, fake it so that we can render a line
				if (maxY == 0) {
					// if both are zero, change the values to prevent division
					// by zero
					maxY = 1.0d;
					minY = 0.0d;
				} else {
					maxY = maxY * 1.05d;
					minY = minY * 0.95d;
				}
			}

			/*
			 * horizontal lines for (int i = 0; i < hors; i++) {
			 * paint.setColor(graphViewStyle.getGridColor()); float x =
			 * ((graphwidth / horlabels.length) * (i+1)) + horstart;
			 * canvas.drawLine(x, height, x, border, paint); }
			 */

			for (int i = 0; i < graphSeries.size(); i++) {
				drawSeries(canvas, _values(i), graphwidth, graphheight, border,
						minX, minY, diffX, diffY, horstart);
			}

			drawHorizontalLabels(canvas, horlabels, graphwidth, graphheight,
					border, horstart);
		}

		private void onMoveGesture(float f) {
			// view port update
			if (viewportSize != 0) {
				viewportStart -= f * viewportSize / graphwidth;

				// minimal and maximal view limit
				double minX = getMinX(true);
				double maxX = getMaxX(true);
				if (viewportStart < minX) {
					viewportStart = minX;
				} else if (viewportStart + viewportSize > maxX) {
					viewportStart = maxX - viewportSize;
				}

				// labels have to be regenerated
				if (!staticHorizontalLabels)
					horlabels = null;
			}
			invalidate();
		}

		/**
		 * @param event
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (!isScrollable() || isDisableTouch()) {
				return super.onTouchEvent(event);
			}

			boolean handled = false;

			if (!handled) {
				// Log.d("GraphView",
				// "on touch event scale not handled+"+lastTouchEventX);
				// if not scaled, scroll
				if ((event.getAction() & MotionEvent.ACTION_DOWN) == MotionEvent.ACTION_DOWN) {
					scrollingStarted = true;
					handled = true;
				}
				if ((event.getAction() & MotionEvent.ACTION_UP) == MotionEvent.ACTION_UP) {
					scrollingStarted = false;
					lastTouchEventX = 0;
					handled = true;
				}
				if ((event.getAction() & MotionEvent.ACTION_MOVE) == MotionEvent.ACTION_MOVE) {
					if (scrollingStarted) {
						if (lastTouchEventX != 0) {
							onMoveGesture(event.getX() - lastTouchEventX);
						}
						lastTouchEventX = event.getX();
						handled = true;
					}
				}
				if (handled)
					invalidate();
			} else {
				// currently scaling
				scrollingStarted = false;
				lastTouchEventX = 0;
			}
			return handled;
		}
	}

	/**
	 * one data set for a graph series
	 */
	static public class GraphViewData implements GraphViewDataInterface {
		public double valueX;
		public double valueY;

		public GraphViewData(double valueX, double valueY) {
			super();
			this.valueX = valueX;
			this.valueY = valueY;
		}

		@Override
		public double getX() {
			return valueX;
		}

		@Override
		public double getY() {
			return valueY;
		}
	}

	public enum LegendAlign {
		TOP, MIDDLE, BOTTOM
	}

	protected final Paint paint;
	private String[] horlabels;
	private boolean scrollable;
	private boolean disableTouch;
	private double viewportStart;
	private double viewportSize;
	private final NumberFormat[] numberformatter = new NumberFormat[2];
	private final List<GraphViewSeries> graphSeries;
	private boolean showLegend = false;
	private float legendWidth = 120;
	private LegendAlign legendAlign = LegendAlign.MIDDLE;
	private boolean manualYAxis;
	private double manualMaxYValue;
	private double manualMinYValue;
	private GraphViewStyle graphViewStyle;
	private final GraphViewContentView graphViewContentView;
	private Integer labelTextHeight;
	private Integer horLabelTextWidth;
	private final Rect textBounds = new Rect();
	private boolean staticHorizontalLabels;

	public GraphView(Context context, AttributeSet attrs) {
		this(context);

		int width = attrs.getAttributeIntValue("android", "layout_width",
				LayoutParams.MATCH_PARENT);
		int height = attrs.getAttributeIntValue("android", "layout_height",
				LayoutParams.MATCH_PARENT);
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
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		graphViewStyle = new GraphViewStyle();

		paint = new Paint();
		graphSeries = new ArrayList<GraphViewSeries>();

		// viewVerLabels = new VerLabelsView(context);
		// addView(viewVerLabels);
		graphViewContentView = new GraphViewContentView(context);
		addView(graphViewContentView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
	}

	private GraphViewDataInterface[] _values(int idxSeries) {
		GraphViewDataInterface[] values = graphSeries.get(idxSeries).values;
		synchronized (values) {
			if (viewportStart == 0 && viewportSize == 0) {
				// all data
				return values;
			} else {
				// viewport
				List<GraphViewDataInterface> listData = new ArrayList<GraphViewDataInterface>();
				for (int i = 0; i < values.length; i++) {
					if (values[i].getX() >= viewportStart) {
						if (values[i].getX() > viewportStart + viewportSize) {
							listData.add(values[i]); // one more for nice
														// scrolling
							break;
						} else {
							listData.add(values[i]);
						}
					} else {
						if (listData.isEmpty()) {
							listData.add(values[i]);
						}
						listData.set(0, values[i]); // one before, for nice
													// scrolling
					}
				}
				return listData.toArray(new GraphViewDataInterface[listData
						.size()]);
			}
		}
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

	public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight, float border, double minX, double minY, double diffX, double diffY, float horstart) {
		paint.setColor(getResources().getColor(R.color.yellow));
		paint.setStrokeWidth(3.0f);
		
		// draw background
		double lastEndY = minY / diffY * graphwidth;
		double lastEndX = minX / diffX * graphwidth;
		
		 Resources res = getResources();
		 Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.baloon);
		
		float startY = graphheight + border;
		for (int i = 0; i < values.length; i++) {
			double valY = values[i].getY() - minY;
			double ratY = valY / diffY;
			double y = graphheight * ratY;

			double valX = values[i].getX() - minX;
			double ratX = valX / diffX;
			double x = graphwidth * ratX;

			float endX = (float) x + (horstart + 1);
			float endY = (float) (border - y) + graphheight +2;

			if (i > 0) {
				// fill space between last and current point
				double numSpace = ((endX - lastEndX) / 3f) +1;
				for (int xi = 0; xi < numSpace; xi++) {
					float spaceX = (float) (lastEndX + ((endX-lastEndX)*xi/(numSpace-1)));
					float spaceY = (float) (lastEndY + ((endY-lastEndY)*xi/(numSpace-1)));

					// start => bottom edge
					float startX = spaceX;

					// do not draw over the left edge
					if (startX-horstart > 1) {
						canvas.drawLine(startX, startY, spaceX, spaceY, paint);
						canvas.drawBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true), startX, startY, paint);
					}
				}
			}

			lastEndY = endY;
			lastEndX = endX;
		}
	}

	protected void drawHorizontalLabels(Canvas canvas, String[] horlabels,
			float graphwidth, float graphheight, float border, float horstart) {
		paint.setTextSize(20f);
		paint.setTextAlign(Align.LEFT);

		int hors = horlabels.length - 1;
		if (hors < 1)
			hors = 1;

		paint.setColor(getResources().getColor(R.color.black));
		paint.setStyle(Style.FILL);
		canvas.drawRect(0, graphheight + border, graphwidth, graphheight
				+ (2 * border), paint);

		for (int i = 0; i < horlabels.length; i++) {
			paint.setTextAlign(Align.CENTER);
			float x_text = ((graphwidth / (horlabels.length * 2)) * ((i * 2) + 1))
					+ horstart;
			paint.setColor(getResources().getColor(R.color.white));
			canvas.drawText(horlabels[i], x_text, graphheight + border
					+ (border / 2), paint);
		}
	}

	/**
	 * @return the graphview style. it will never be null.
	 */
	public GraphViewStyle getGraphViewStyle() {
		return graphViewStyle;
	}

	/**
	 * get the position of the legend
	 * 
	 * @return
	 */
	public LegendAlign getLegendAlign() {
		return legendAlign;
	}

	/**
	 * @return legend width
	 */
	public float getLegendWidth() {
		return legendWidth;
	}

	/**
	 * returns the maximal X value of the current viewport (if viewport is set)
	 * otherwise maximal X value of all data.
	 * 
	 * @param ignoreViewport
	 * 
	 *            warning: only override this, if you really know want you're
	 *            doing!
	 */
	protected double getMaxX(boolean ignoreViewport) {
		// if viewport is set, use this
		if (!ignoreViewport && viewportSize != 0) {
			return viewportStart + viewportSize;
		} else {
			// otherwise use the max x value
			// values must be sorted by x, so the last value has the largest X
			// value
			double highest = 0;
			if (graphSeries.size() > 0) {
				GraphViewDataInterface[] values = graphSeries.get(0).values;

				if (values.length == 0)
					highest = 0;
				else
					highest = values[values.length - 1].getX();

				for (int i = 1; i < graphSeries.size(); i++) {
					values = graphSeries.get(i).values;

					if (values.length > 0) {
						highest = Math.max(highest,
								values[values.length - 1].getX());
					}
				}
			}
			return highest;
		}
	}

	/**
	 * returns the maximal Y value of all data.
	 * 
	 * warning: only override this, if you really know want you're doing!
	 */
	protected double getMaxY() {
		double largest;
		if (manualYAxis) {
			largest = manualMaxYValue;
		} else {
			largest = Integer.MIN_VALUE;
			for (int i = 0; i < graphSeries.size(); i++) {
				GraphViewDataInterface[] values = _values(i);
				for (int ii = 0; ii < values.length; ii++) {
					if (values[ii].getY() > largest) {
						largest = values[ii].getY();
					}
				}
			}
		}
		return largest;
	}

	/**
	 * returns the minimal X value of the current viewport (if viewport is set)
	 * otherwise minimal X value of all data.
	 * 
	 * @param ignoreViewport
	 * 
	 *            warning: only override this, if you really know want you're
	 *            doing!
	 */
	protected double getMinX(boolean ignoreViewport) {
		// if viewport is set, use this
		if (!ignoreViewport && viewportSize != 0) {
			return viewportStart;
		} else {
			// otherwise use the min x value
			// values must be sorted by x, so the first value has the smallest X
			// value
			double lowest = 0;
			if (graphSeries.size() > 0) {
				GraphViewDataInterface[] values = graphSeries.get(0).values;
				if (values.length == 0)
					lowest = 0;
				else
					lowest = values[0].getX();

				for (int i = 1; i < graphSeries.size(); i++) {
					values = graphSeries.get(i).values;
					if (values.length > 0) {
						lowest = Math.min(lowest, values[0].getX());
					}
				}
			}
			return lowest;
		}
	}

	/**
	 * returns the minimal Y value of all data.
	 * 
	 * warning: only override this, if you really know want you're doing!
	 */
	protected double getMinY() {
		double smallest;
		if (manualYAxis) {
			smallest = manualMinYValue;
		} else {
			smallest = Integer.MAX_VALUE;
			for (int i = 0; i < graphSeries.size(); i++) {
				GraphViewDataInterface[] values = _values(i);
				for (int ii = 0; ii < values.length; ii++)
					if (values[ii].getY() < smallest)
						smallest = values[ii].getY();
			}
		}
		return smallest;
	}

	public boolean isDisableTouch() {
		return disableTouch;
	}

	public boolean isScrollable() {
		return scrollable;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	/**
	 * forces graphview to invalide all views and caches. Normally there is no
	 * need to call this manually.
	 */
	public void redrawAll() {
		if (!staticHorizontalLabels)
			horlabels = null;
		numberformatter[0] = null;
		numberformatter[1] = null;
		labelTextHeight = null;
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
	 * scrolls to the last x-value
	 * 
	 * @throws IllegalStateException
	 *             if scrollable == false
	 */
	public void scrollToEnd() {
		if (!scrollable)
			throw new IllegalStateException("This GraphView is not scrollable.");
		double max = getMaxX(true);
		viewportStart = max - viewportSize;
		redrawAll();
	}

	/**
	 * The user can disable any touch gestures, this is useful if you are using
	 * a real time graph, but don't want the user to interact
	 * 
	 * @param disableTouch
	 */
	public void setDisableTouch(boolean disableTouch) {
		this.disableTouch = disableTouch;
	}

	/**
	 * set custom graphview style
	 * 
	 * @param style
	 */
	public void setGraphViewStyle(GraphViewStyle style) {
		graphViewStyle = style;
		labelTextHeight = null;
	}

	/**
	 * set's static horizontal labels (from left to right)
	 * 
	 * @param horlabels
	 *            if null, labels were generated automatically
	 */
	public void setHorizontalLabels(String[] horlabels) {
		staticHorizontalLabels = horlabels != null;
		this.horlabels = horlabels;
	}

	/**
	 * legend position
	 * 
	 * @param legendAlign
	 */
	public void setLegendAlign(LegendAlign legendAlign) {
		this.legendAlign = legendAlign;
	}

	/**
	 * legend width
	 * 
	 * @param legendWidth
	 */
	public void setLegendWidth(float legendWidth) {
		this.legendWidth = legendWidth;
	}

	/**
	 * you have to set the bounds {@link #setManualYAxisBounds(double, double)}.
	 * That automatically enables manualYAxis-flag. if you want to disable the
	 * menual y axis, call this method with false.
	 * 
	 * @param manualYAxis
	 */
	public void setManualYAxis(boolean manualYAxis) {
		this.manualYAxis = manualYAxis;
	}

	/**
	 * set manual Y axis limit
	 * 
	 * @param max
	 * @param min
	 */
	public void setManualYAxisBounds(double max, double min) {
		manualMaxYValue = max;
		manualMinYValue = min;
		manualYAxis = true;
	}

	/**
	 * the user can scroll (horizontal) the graph. This is only useful if you
	 * use a viewport {@link #setViewPort(double, double)} which doesn't
	 * displays all data.
	 * 
	 * @param scrollable
	 */
	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	/**
	 * set's the viewport for the graph.
	 * 
	 * @see #setManualYAxisBounds(double, double) to limit the y-viewport
	 * @param start
	 *            x-value
	 * @param size
	 */
	public void setViewPort(double start, double size) {
		viewportStart = start;
		viewportSize = size;
	}
}
