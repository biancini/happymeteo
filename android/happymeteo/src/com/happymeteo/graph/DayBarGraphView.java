package com.happymeteo.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

import com.happymeteo.R;


public class DayBarGraphView extends GraphView {
	public DayBarGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DayBarGraphView(Context context) {
		super(context);
	}
	
	private int getColor(int value) {
		switch (value) {
		case 1:
			return 0xFF6ab2e3;
		case 2:
			return 0xFF3386c7;
		case 3:
			return 0xFF385ea8;
		case 4:
			return 0xFF703e91;
		case 5:
			return 0xFFcb3176;
		case 6:
			return 0xFFe7334b;
		case 7:
			return 0xFFea5a1a;
		case 8:
			return 0xFFf0801e;
		case 9:
			return 0xFFf6ab2b;
		case 10:
			return 0xFFfdd606;
		}

		return 0xFF6ab2e3;
	}

	@Override
	public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight,
			float border, double minX, double minY, double diffX, double diffY,
			float horstart) {
		float colwidth = (graphwidth/* - (2 * border)*/) / values.length;
		
		paint.setTextSize(10f);

		// draw data
		for (int i = 0; i < values.length; i++) {
			float valY = (float) (values[i].getY() - minY);
			float ratY = (float) (valY / diffY);
			float y = graphheight * ratY;

			paint.setColor(getColor((int) values[i].getY()));
			canvas.drawRect((i * colwidth) + horstart, (border - y) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), graphheight + border - 1, paint);
			paint.setColor(getResources().getColor(R.color.white));
			canvas.drawText(String.valueOf(i+1), (i * colwidth) + horstart + colwidth/2, graphheight + border + 10, paint);
		}
	}

	@Override
	protected void drawHorizontalLabels(Canvas canvas, String[] horlabels, float graphwidth, float border, float horstart, float height) {
		paint.setTextSize(20f);
		paint.setTextAlign(Align.LEFT);
		// horizontal labels + lines
		int hors = horlabels.length - 1;
		if(hors < 1) {
			hors = 1;
		}
		for (int i = 0; i < horlabels.length; i++) {
			paint.setColor(getResources().getColor(R.color.black));
			paint.setStyle(Style.FILL);
			float x = ((graphwidth / hors) * i) + horstart;
			float x_next = ((graphwidth / hors) * (i+1)) + horstart;
			canvas.drawRect(x, height, x_next, height - border, paint);
			paint.setTextAlign(Align.CENTER);
			float x_text = ((graphwidth / (horlabels.length*2)) * ((i*2)+1)) + horstart;
			paint.setColor(getResources().getColor(R.color.white));
			canvas.drawText(horlabels[i], x_text, height - 8, paint);
		}
	}
}