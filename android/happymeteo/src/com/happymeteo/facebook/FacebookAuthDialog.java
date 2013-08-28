/**
 Copyright 2010-present Facebook.
 *
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 *
    http://www.apache.org/licenses/LICENSE-2.0
 *
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.happymeteo.facebook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.FacebookDialogException;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookServiceException;
import com.facebook.android.Util;
import com.facebook.internal.Utility;
import com.happymeteo.R;
import com.happymeteo.utils.Const;

/**
 * This class provides a mechanism for displaying Facebook Web dialogs inside a
 * Dialog. Helper methods are provided to construct commonly-used dialogs, or a
 * caller can specify arbitrary parameters to call other dialogs.
 */
public class FacebookAuthDialog extends Dialog {
	private static final String LOG_TAG = Const.TAG;
	private static final String DISPLAY_TOUCH = "touch";
	private static final String USER_AGENT = "user_agent";
	static final String REDIRECT_URI = Const.BASE_URL;
	static final String CANCEL_URI = "fbconnect://cancel";
	static final boolean DISABLE_SSL_CHECK_FOR_TESTING = false;

	public static final int DEFAULT_THEME = android.R.style.Theme_Translucent_NoTitleBar;

	private Activity activity;
	private String url;
	private OnCompleteListener onCompleteListener;
	private WebView webView;
	private ProgressDialog spinner;
	private ImageView crossImageView;
	private FrameLayout contentFrameLayout;
	private boolean listenerCalled = false;
	private boolean isDetached = false;

	/**
	 * Interface that implements a listener to be called when the user's
	 * interaction with the dialog completes, whether because the dialog
	 * finished successfully, or it was cancelled, or an error was encountered.
	 */
	public interface OnCompleteListener {
		/**
		 * Called when the dialog completes.
		 * 
		 * @param values
		 *            on success, contains the values returned by the dialog
		 * @param error
		 *            on an error, contains an exception describing the error
		 */
		void onComplete(Bundle values, FacebookException error);
	}

	/**
	 * Constructor which can be used to display a dialog with an
	 * already-constructed URL.
	 * 
	 * @param context
	 *            the context to use to display the dialog
	 * @param url
	 *            the URL of the Web Dialog to display; no validation is done on
	 *            this URL, but it should be a valid URL pointing to a Facebook
	 *            Web Dialog
	 */
	public FacebookAuthDialog(Activity activity, String url) {
		this(activity, url, DEFAULT_THEME);
	}

	/**
	 * Constructor which can be used to display a dialog with an
	 * already-constructed URL and a custom theme.
	 * 
	 * @param context
	 *            the context to use to display the dialog
	 * @param url
	 *            the URL of the Web Dialog to display; no validation is done on
	 *            this URL, but it should be a valid URL pointing to a Facebook
	 *            Web Dialog
	 * @param theme
	 *            identifier of a theme to pass to the Dialog class
	 */
	public FacebookAuthDialog(Activity activity, String url, int theme) {
		super(activity, theme);
		this.url = url;
		this.activity = activity;
	}

	/**
	 * Sets the listener which will be notified when the dialog finishes.
	 * 
	 * @param listener
	 *            the listener to notify, or null if no notification is desired
	 */
	public void setOnCompleteListener(OnCompleteListener listener) {
		onCompleteListener = listener;
	}

	/**
	 * Gets the listener which will be notified when the dialog finishes.
	 * 
	 * @return the listener, or null if none has been specified
	 */
	public OnCompleteListener getOnCompleteListener() {
		return onCompleteListener;
	}

	@Override
	public void dismiss() {
		if (webView != null) {
			webView.stopLoading();
		}
		if (!isDetached) {
			if (spinner.isShowing()) {
				spinner.dismiss();
			}
			super.dismiss();
		}
	}

	@Override
	public void onDetachedFromWindow() {
		isDetached = true;
		super.onDetachedFromWindow();
	}

	@Override
	public void onAttachedToWindow() {
		isDetached = false;
		super.onAttachedToWindow();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				sendCancelToListener();
			}
		});

		spinner = new ProgressDialog(getContext());
		spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		spinner.setMessage(getContext().getString(R.string.loading));
		spinner.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				sendCancelToListener();
				FacebookAuthDialog.this.dismiss();
			}
		});

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		contentFrameLayout = new FrameLayout(getContext());

		/*
		 * Create the 'x' image, but don't add to the contentFrameLayout layout
		 * yet at this point, we only need to know its drawable width and height
		 * to place the webview
		 */
		createCrossImage();

		/*
		 * Now we know 'x' drawable width and height, layout the webivew and add
		 * it the contentFrameLayout layout
		 */
		int crossWidth = crossImageView.getDrawable().getIntrinsicWidth();
		setUpWebView(crossWidth / 2);

		/*
		 * Finally add the 'x' image to the contentFrameLayout layout and add
		 * contentFrameLayout to the Dialog view
		 */
		contentFrameLayout.addView(crossImageView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		addContentView(contentFrameLayout, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

	private void sendSuccessToListener(Bundle values) {
		if (onCompleteListener != null && !listenerCalled) {
			listenerCalled = true;
			onCompleteListener.onComplete(values, null);
		}
	}

	private void sendErrorToListener(Throwable error) {
		if (onCompleteListener != null && !listenerCalled) {
			listenerCalled = true;
			FacebookException facebookException = null;
			if (error instanceof FacebookException) {
				facebookException = (FacebookException) error;
			} else {
				facebookException = new FacebookException(error);
			}
			onCompleteListener.onComplete(null, facebookException);
		}
	}

	private void sendCancelToListener() {
		sendErrorToListener(new FacebookOperationCanceledException());
	}

	private void createCrossImage() {
		crossImageView = new ImageView(getContext());
		// Dismiss the dialog when user click on the 'x'
		crossImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCancelToListener();
				FacebookAuthDialog.this.dismiss();
			}
		});
		Drawable crossDrawable = getContext().getResources().getDrawable(
				R.drawable.com_facebook_close);
		crossImageView.setImageDrawable(crossDrawable);
		/*
		 * 'x' should not be visible while webview is loading make it visible
		 * only after webview has fully loaded
		 */
		crossImageView.setVisibility(View.INVISIBLE);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebView(int margin) {
		final DialogWebViewClient dialogWebViewClient = new DialogWebViewClient();

		LinearLayout webViewContainer = new LinearLayout(getContext());
		webView = new WebView(getContext());
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setWebViewClient(dialogWebViewClient);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JavaScriptInterface() {
			public void showHTML(String html) {
				Log.i(Const.TAG, "showHTML.length(): " + html.length());
				Log.i(Const.TAG,
						"check error: "
								+ html.contains("Torna alla pagina precedente"));

				if (!listenerCalled) {
					if (html.contains("Torna alla pagina precedente")) {
						webView.loadUrl(url);
						Log.i(Const.TAG,
								"showHTML: "
										+ html.substring(html
												.indexOf("Torna alla pagina precedente") - 1000));
					} else {
						activity.runOnUiThread(new Runnable() {
							public void run() {
								contentFrameLayout.setBackgroundColor(Color.TRANSPARENT);
								webView.setVisibility(View.VISIBLE);
								crossImageView.setVisibility(View.VISIBLE);
							}
						});
					}
				}
			}
		}, "MY_JS");
		webView.loadUrl(url);
		webView.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		webView.setVisibility(View.INVISIBLE);
		webView.getSettings().setSavePassword(false);

		webViewContainer.setPadding(margin, margin, margin, margin);
		webViewContainer.addView(webView);
		contentFrameLayout.addView(webViewContainer);
	}

	private class DialogWebViewClient extends WebViewClient {

		@Override
		@SuppressWarnings("deprecation")
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i(LOG_TAG, "Redirect URL: " + url);
			if (url.startsWith(FacebookAuthDialog.REDIRECT_URI)) {
				Bundle values = Util.parseUrl(url);

				String error = values.getString("error");
				if (error == null) {
					error = values.getString("error_type");
				}

				String errorMessage = values.getString("error_msg");
				if (errorMessage == null) {
					errorMessage = values.getString("error_description");
				}
				String errorCodeString = values.getString("error_code");
				int errorCode = FacebookRequestError.INVALID_ERROR_CODE;
				if (!Utility.isNullOrEmpty(errorCodeString)) {
					try {
						errorCode = Integer.parseInt(errorCodeString);
					} catch (NumberFormatException ex) {
						errorCode = FacebookRequestError.INVALID_ERROR_CODE;
					}
				}

				if (Utility.isNullOrEmpty(error)
						&& Utility.isNullOrEmpty(errorMessage)
						&& errorCode == FacebookRequestError.INVALID_ERROR_CODE) {
					sendSuccessToListener(values);
				} else if (error != null
						&& (error.equals("access_denied") || error
								.equals("OAuthAccessDeniedException"))) {
					sendCancelToListener();
				} else {
					FacebookRequestError requestError = new FacebookRequestError(
							errorCode, error, errorMessage);
					sendErrorToListener(new FacebookServiceException(
							requestError, errorMessage));
				}

				FacebookAuthDialog.this.dismiss();
				return true;
			} else if (url.startsWith(FacebookAuthDialog.CANCEL_URI)) {
				sendCancelToListener();
				FacebookAuthDialog.this.dismiss();
				return true;
			}
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			sendErrorToListener(new FacebookDialogException(description,
					errorCode, failingUrl));
			FacebookAuthDialog.this.dismiss();
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			if (DISABLE_SSL_CHECK_FOR_TESTING) {
				handler.proceed();
			} else {
				super.onReceivedSslError(view, handler, error);

				sendErrorToListener(new FacebookDialogException(null,
						ERROR_FAILED_SSL_HANDSHAKE, null));
				handler.cancel();
				FacebookAuthDialog.this.dismiss();
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.i(LOG_TAG, "Webview loading URL: " + url);
			super.onPageStarted(view, url, favicon);
			if (!isDetached) {
				spinner.show();
				webView.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.i(LOG_TAG, "Webview finished URL: " + url);
			super.onPageFinished(view, url);
			if (!isDetached) {
				spinner.dismiss();
			}
			/*
			 * Once web view is fully loaded, set the contentFrameLayout
			 * background to be transparent and make visible the 'x' image.
			 */
			view.loadUrl("javascript:window.MY_JS.showHTML(document.getElementsByTagName('html')[0].innerHTML);");
		}
	}
}
