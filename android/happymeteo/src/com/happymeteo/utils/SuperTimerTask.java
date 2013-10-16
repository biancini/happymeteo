package com.happymeteo.utils;

import java.util.Timer;
import java.util.TimerTask;

public abstract class SuperTimerTask extends TimerTask {
	private int count;
	private Timer timer;
	
	public SuperTimerTask(Timer timer) {
		this.count = 0;
		this.timer = timer;
	}

	@Override
	public void run() {
		count++;
		if (count >= 6) {
			timer.cancel();
			timer.purge();
			return;
		}
		
		doOperation();
	}
	
	abstract void doOperation();
}
