package com.ucpaas.chat.activity;

import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

/**
 * 欢迎页面
 * 
 * @author tangqi
 * @date 2015年8月31日下午9:30:30
 */

public class SplashActivity extends BaseActivity {

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_welcome);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				SplashActivity.this.finish();
			}
		}, 2000);
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub

	}

}
