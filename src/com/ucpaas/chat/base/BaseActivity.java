package com.ucpaas.chat.base;

import com.ucpaas.chat.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Activity基类
 * 
 * @author tangqi
 * @date 2015年8月31日下午9:34:00
 */

public abstract class BaseActivity extends Activity implements OnClickListener {

	/**
	 * 定义OnCreate方法的处理逻辑
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentLayout();
		beforeInitView();
		initHeader();
		initView();
		afterInitView();
	}

	/**
	 * 初始化Header布局
	 */
	private void initHeader() {
		try {
			ImageView btnBack = (ImageView) findViewById(R.id.btn_back);
			btnBack.setOnClickListener(this);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 公共点击事件，比如返回
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		// 处理左上角返回键
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}

		onViewClick(v);
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

	/**
	 * 显示右边菜单
	 */
	protected void showRightMenu() {
		try {
			ImageView btnMenu = (ImageView) findViewById(R.id.btn_menu);
			btnMenu.setVisibility(View.VISIBLE);
			btnMenu.setOnClickListener(this);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	protected void setTitle(String title) {
		try {
			TextView tvTitle = (TextView) findViewById(R.id.tv_title);
			tvTitle.setText(title);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 设置布局文件
	 */
	public abstract void setContentLayout();

	/**
	 * 在实例化布局之前处理的逻辑
	 */
	public abstract void beforeInitView();

	/**
	 * 实例化布局文件/组件
	 */
	public abstract void initView();

	/**
	 * 在实例化布局之后处理的逻辑
	 */
	public abstract void afterInitView();

	/**
	 * 处理点击事件
	 */
	public abstract void onViewClick(View v);
}
