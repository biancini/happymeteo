package com.happymeteo.meteo;

public class GraphViewData {
	public float valueX;
	public float valueY;

	public GraphViewData(float valueX, float valueY) {
		super();
		this.valueX = valueX;
		this.valueY = valueY;
	}

	public float getX() {
		return valueX;
	}

	public float getY() {
		return valueY;
	}
}
