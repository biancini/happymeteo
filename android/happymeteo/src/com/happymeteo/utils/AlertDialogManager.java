package com.happymeteo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.happymeteo.R;

public class AlertDialogManager {
	static public void showError(Activity activity, String error) {
		new AlertDialog.Builder(activity)
		.setTitle(activity.getString(com.happymeteo.R.string.error))
		.setMessage(error)
		.setPositiveButton(activity.getString(com.happymeteo.R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
			}
		})
		.setIcon(R.drawable.fail)
		.show();
	}
	
	static public void showErrorAndRetry(Activity activity, String error, DialogInterface.OnClickListener retryClickListener) {
		new AlertDialog.Builder(activity)
		.setTitle(activity.getString(com.happymeteo.R.string.error))
		.setMessage(error)
		.setPositiveButton(activity.getString(com.happymeteo.R.string.retry), retryClickListener)
		.setNegativeButton(activity.getString(com.happymeteo.R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
			}
		})
		.setIcon(R.drawable.fail)
		.show();
	}
	
	static public void showNotification(Activity activity, int title, int message, DialogInterface.OnClickListener clickListener) {
		new AlertDialog.Builder(activity)
		.setTitle(activity.getString(title))
		.setMessage(activity.getString(message))
		.setPositiveButton(activity.getString(com.happymeteo.R.string.ok), clickListener)
		.setIcon(R.drawable.success)
		.show();
	}
}
