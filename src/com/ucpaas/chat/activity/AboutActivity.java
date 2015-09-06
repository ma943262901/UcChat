package com.ucpaas.chat.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;

/**
 * 关于
 * 
 * @author tangqi
 * @date 2015年9月7日上午5:51:07
 */

public class AboutActivity extends BaseActivity {

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_about);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		TextView mTitleView = (TextView) findViewById(R.id.tv_title);
		mTitleView.setText("关于");

		ImageView mBackBtn = (ImageView) findViewById(R.id.btn_back);
		mBackBtn.setVisibility(View.VISIBLE);
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		WebView mWebView = (WebView) findViewById(R.id.webview_about);
		mWebView.loadUrl("file:///android_asset/html/about.html");
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub

	}

}
