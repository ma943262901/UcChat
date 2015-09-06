package com.ucpaas.chat.base;

import com.ucpaas.chat.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * FragmentActivity-基类
 * 
 * @author tangqi
 * @data 2015年8月10日上午12:07:57
 */

public class BaseFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		BaseApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * 隐藏返回按钮
	 */
	protected void hideBackButton() {
		try {
			ImageView btnBack = (ImageView) findViewById(R.id.btn_back);
			ImageView imvDivider = (ImageView) findViewById(R.id.imv_actionbar_divider);

			btnBack.setVisibility(View.GONE);
			imvDivider.setVisibility(View.GONE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	
}
