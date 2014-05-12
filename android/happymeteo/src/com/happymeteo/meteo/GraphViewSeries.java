/**
 * This file is part of GraphView.
 *
 * GraphView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GraphView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GraphView.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 *
 * Copyright Jonas Gehring
 */

package com.happymeteo.meteo;

import java.util.ArrayList;
import java.util.List;

/**
 * a graphview series.
 * holds the data, description and styles
 */
public class GraphViewSeries {
	/**
	 * graph series style: color and thickness
	 */
	static public class GraphViewSeriesStyle {
		public int color = 0xff0077cc;
		public int thickness = 3;

		public GraphViewSeriesStyle() {
			super();
		}
		
		public GraphViewSeriesStyle(int color, int thickness) {
			super();
			this.color = color;
			this.thickness = thickness;
		}
	}

	GraphViewData[] values = null;
	private final List<GraphView> graphViews = new ArrayList<GraphView>();
	final GraphViewSeriesStyle style;

	public GraphViewSeries(GraphViewData[] values) {
		style = new GraphViewSeriesStyle();
		this.values = values;
	}

	public GraphViewSeries(GraphViewSeriesStyle style, GraphViewData[] values) {
		super();
		if (style == null) style = new GraphViewSeriesStyle();
		
		this.style = style;
		this.values = values;
	}

	/**
	 * this graphview will be redrawn if data changes
	 * @param graphView
	 */
	public void addGraphView(GraphView graphView) {
		this.graphViews.add(graphView);
	}

	/**
	 * @return series styles. never null
	 */
	public GraphViewSeriesStyle getStyle() {
		return style;
	}

	/**
	 * you should use {@link GraphView#removeSeries(GraphViewSeries)}
	 * @param graphView
	 */
	public void removeGraphView(GraphView graphView) {
		graphViews.remove(graphView);
	}

	/**
	 * clears the current data and set the new.
	 * redraws the graphview(s)
	 * @param values new data
	 */
	public void resetData(GraphViewData[] values) {
		this.values = values;
		for (GraphView g : graphViews) g.redrawAll();
	}
}
