package com.happymeteo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

import com.happymeteo.R;

public class AlertDialogManager {
	static public void showError(Activity activity, String error) {
		try {
			if (!activity.isFinishing()) {
					new AlertDialog.Builder(activity)
					.setTitle(activity.getString(com.happymeteo.R.string.error))
					.setMessage(error)
					.setPositiveButton(activity.getString(com.happymeteo.R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
						}
					})
					.setIcon(R.drawable.fail)
					.show();
			}
		}
		catch (BadTokenException bte) {
			Log.e(Const.TAG, bte.getMessage(), bte);
		}
	}
	
	static public void showErrorAndRetry(Activity activity, String error, DialogInterface.OnClickListener retryClickListener) {
		try {
			if (!activity.isFinishing()) {
				new AlertDialog.Builder(activity)
					.setTitle(activity.getString(com.happymeteo.R.string.error))
					.setMessage(error)
					.setPositiveButton(activity.getString(com.happymeteo.R.string.retry), retryClickListener)
					.setNegativeButton(activity.getString(com.happymeteo.R.string.cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
						}
					})
					.setIcon(R.drawable.fail)
					.show();
			}
		}
		catch (BadTokenException bte) {
			Log.e(Const.TAG, bte.getMessage(), bte);
		}
	}
	
	static public void showNotification(Activity activity, int title, int message, DialogInterface.OnClickListener clickListener) {
		try {
			if (!activity.isFinishing()) {
				new AlertDialog.Builder(activity)
					.setTitle(activity.getString(title))
					.setMessage(activity.getString(message))
					.setPositiveButton(activity.getString(com.happymeteo.R.string.ok), clickListener)
					.setIcon(R.drawable.success)
					.show();
			}
		}
		catch (BadTokenException bte) {
			Log.e(Const.TAG, bte.getMessage(), bte);
		}
	}
}
