package com.happymeteo.meteo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class GraphViewLayout extends LinearLayout {
	private final GraphViewContentView graphViewContentView;

	public GraphViewLayout(Context context, AttributeSet attrs) {
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
	public GraphViewLayout(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		graphViewContentView = new GraphViewContentView(context);
		addView(graphViewContentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
	}
	
	public void setScale(float scale) {
		graphViewContentView.setScale(scale);
	}
	
	public void setGraphData(float graphData[], String[] horlabels) {
		graphViewContentView.setGraphData(graphData);
		graphViewContentView.setHorlabels(horlabels);
	}

	/**
	 * forces graphview to invalide all views and caches. Normally there is no
	 * need to call this manually.
	 */
	public void redrawAll() {
		invalidate();
		graphViewContentView.invalidate();
	}
	
}
