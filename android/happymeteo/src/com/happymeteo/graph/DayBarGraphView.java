package com.happymeteo.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;

import com.happymeteo.R;
import com.happymeteo.utils.Const;


public class DayBarGraphView extends GraphView {
	public DayBarGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DayBarGraphView(Context context) {
		super(context);
	}
	
//	private int getColor(int value) {
//		switch (value) {
//		case 1:
//			return 0xFF6ab2e3;
//		case 2:
//			return 0xFF3386c7;
//		case 3:
//			return 0xFF385ea8;
//		case 4:
//			return 0xFF703e91;
//		case 5:
//			return 0xFFcb3176;
//		case 6:
//			return 0xFFe7334b;
//		case 7:
//			return 0xFFea5a1a;
//		case 8:
//			return 0xFFf0801e;
//		case 9:
//			return 0xFFf6ab2b;
//		case 10:
//			return 0xFFfdd606;
//		}
//
//		return 0xFF6ab2e3;
//	}

//	@Override
//	public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight,
//			float border, double minX, double minY, double diffX, double diffY,
//			float horstart) {
//		float colwidth = (graphwidth/* - (2 * border)*/) / values.length;
//		
//		paint.setTextSize(10f);
//
//		// draw data
//		for (int i = 0; i < values.length; i++) {
//			float valY = (float) (values[i].getY() - minY);
//			float ratY = (float) (valY / diffY);
//			float y = graphheight * ratY;
//
////			paint.setColor(getColor((int) values[i].getY()));
//			paint.setColor(getResources().getColor(R.color.yellow));
//			canvas.drawRect((i * colwidth) + horstart, (border - y) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), graphheight + border - 1, paint);
//			paint.setColor(getResources().getColor(R.color.white));
//			canvas.drawText(String.valueOf(i+1), (i * colwidth) + horstart + colwidth/2, graphheight + border + 10, paint);
//		}
//	}
	
	@Override
	public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight, float border, double minX, double minY, double diffX, double diffY, float horstart) {
		float colwidth = (graphwidth/* - (2 * border)*/) / values.length;
		paint.setTextSize(10f);
		paint.setStrokeWidth(3.0f);
		
		// draw background
		double lastEndY = minY / diffY * graphwidth;
		double lastEndX = minX / diffX * graphwidth;
		
		boolean stop = false;
		boolean first = true;
		
		float startY = graphheight + border;
		for (int i = 0; i < values.length; i++) {
			paint.setColor(getResources().getColor(R.color.yellow));
			
			if(values[i].getY() == 0) {
				stop = true;
			}
			
			if(!stop) {
				double valY = values[i].getY() - minY;
				double ratY = valY / diffY;
				double y = graphheight * ratY;
	
				double valX = values[i].getX() - minX;
				double ratX = valX / diffX;
				double x = graphwidth * ratX;
	
				float endX = (float) x + (horstart + 1);
				float endY = (float) (border - y) + graphheight +2;
	
	//			if (i > 0) {
					// fill space between last and current point
					double numSpace = ((endX - lastEndX) / 3f) +1;
					for (int xi=0; xi<numSpace; xi++) {
						float spaceX = (float) (lastEndX + ((endX-lastEndX)*xi/(numSpace-1)));
						float spaceY = (float) (lastEndY + ((endY-lastEndY)*xi/(numSpace-1)));
	
						// start => bottom edge
						float startX = spaceX;
	
						// do not draw over the left edge
						if (startX-horstart > 1) {
							if(first) {
								Log.i(Const.TAG, startX+" "+startY+" "+spaceX+" "+spaceY);
								canvas.drawLine(startX, startY, spaceX, spaceY, paint);
							} else {
								first = false;
								canvas.drawLine(startX, startY, spaceX, spaceY, paint);
							}
						}
					}
	//			}
	
				lastEndY = endY;
				lastEndX = endX;
			}
			
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