package com.happymeteo.utils;

import com.happymeteo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {
	/**
	 * Function to display simple Alert Dialog
	 * 
	 * @param context
	 *            - application context
	 * @param title
	 *            - alert dialog title
	 * @param message
	 *            - alert message
	 * @param status
	 *            - success/failure (used to set icon) - pass null if you don't
	 *            want icon
	 * */
	public void showAlertDialog(Context context, String title, String message,
			Boolean status, DialogInterface.OnClickListener clickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		if (status != null)
			// Setting alert dialog icon
			alertDialog
					.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Setting OK Button
		alertDialog.setButton("OK", clickListener);

		// Showing Alert Message
		alertDialog.show();
	}
}