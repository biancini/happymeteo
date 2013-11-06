package com.happymeteo.utils;

import android.content.Context;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

import com.happymeteo.R;

public class SwipeGestureDetector extends SimpleOnGestureListener {
	private ViewFlipper flipper = null;
	private Context context = null;
	private OnSwipeExecuteListener onSwipeExecuteListener = null;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;

	public SwipeGestureDetector(Context context, ViewFlipper flipper, OnSwipeExecuteListener onSwipeExecuteListener) {
		this.flipper = flipper;
		this.context = context;
		this.onSwipeExecuteListener = onSwipeExecuteListener;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;

		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
				&& flipper.getDisplayedChild() == 0) {

			flipper.setInAnimation(context, R.anim.in_from_right);
			flipper.setOutAnimation(context, R.anim.out_to_left);
			onSwipeExecuteListener.OnSwipeExecute(flipper.getDisplayedChild());
			flipper.showNext();

			return true;
		} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
				&& flipper.getDisplayedChild() == 1) {

			flipper.setInAnimation(context, R.anim.in_from_left);
			flipper.setOutAnimation(context, R.anim.out_to_right);
			onSwipeExecuteListener.OnSwipeExecute(flipper.getDisplayedChild());
			flipper.showPrevious();
			
			return true;
		}

		return false;
	}
}