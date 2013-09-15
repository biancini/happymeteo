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
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.happymeteo.AppyMeteoNotLoggedActivity;
import com.happymeteo.utils.Const;

/**
 * This class provides a mechanism for displaying Facebook Web dialogs inside a
 * Dialog. Helper methods are provided to construct commonly-used dialogs, or a
 * caller can specify arbitrary parameters to call other dialogs.
 */
public class AuthDialog extends WebDialog {
	
	public AuthDialog(AppyMeteoNotLoggedActivity activity, String url) {
		super(activity, url);
	}

	@Override
	protected WebViewClient getWebViewClient() {
		return new AuthDialogWebViewClient();
	}
	
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void setUpWebView(int margin) {
		super.setUpWebView(margin);
	}

	private class AuthDialogWebViewClient extends DialogWebViewClient {
		
		@Override
		public void onPageFinished(WebView view, String url) {
			Log.i(Const.TAG, "Webview finished URL: " + url);
			super.onPageFinished(view, url);
			if (!isDetached) {
				spinner.dismiss();
			}
			contentFrameLayout.setBackgroundColor(Color.TRANSPARENT);
			webView.setVisibility(View.VISIBLE);
			crossImageView.setVisibility(View.VISIBLE);
		}
	}
}
