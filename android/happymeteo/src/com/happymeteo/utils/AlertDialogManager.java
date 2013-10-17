package com.happymeteo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.happymeteo.R;

public class AlertDialogManager {
	static public void showError(Context context, String error) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
		.setTitle(context.getString(com.happymeteo.R.string.error))
		.setMessage(error)
		.setPositiveButton(context.getString(com.happymeteo.R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
			}
		})
		.setIcon(R.drawable.fail);
		
		alertDialog.show();
	}
	
	static public void showErrorAndRetry(Context context, String error, DialogInterface.OnClickListener retryClickListener) {
		new AlertDialog.Builder(context)
		.setTitle(context.getString(com.happymeteo.R.string.error))
		.setMessage(error)
		.setPositiveButton(context.getString(com.happymeteo.R.string.retry), retryClickListener)
		.setNegativeButton(context.getString(com.happymeteo.R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
			}
		})
		.setIcon(R.drawable.fail)
		.show();
	}
	
	static public void showNotification(Context context, int title, int message, DialogInterface.OnClickListener clickListener) {
		new AlertDialog.Builder(context)
		.setTitle(context.getString(title))
		.setMessage(context.getString(message))
		.setPositiveButton(context.getString(com.happymeteo.R.string.ok), clickListener)
		.setIcon(R.drawable.success)
		.show();
	}
}
