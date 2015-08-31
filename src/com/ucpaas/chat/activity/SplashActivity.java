package com.ucpaas.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;

/**
 * 闪屏页面
 * 
 * @author tangqi
 * @date 2015年8月31日下午9:30:30
 */

public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				SplashActivity.this.finish();
			}
		}, 2000);
	}

}
