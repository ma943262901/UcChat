package com.ucpaas.chat.activity;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Request;
import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.config.AppConstants;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.OkHttpClientManager;
import com.ucpaas.chat.util.ToastUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * 登录界面
 * 
 * @author tangqi
 * @date 2015年8月31日下午10:06:39
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText mEtUserName;
	@SuppressWarnings("unused")
	private EditText mEtUserPwd;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_login);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mEtUserName = (EditText) findViewById(R.id.login_admin);
		mEtUserPwd = (EditText) findViewById(R.id.login_pwd);

		findViewById(R.id.login_btn).setOnClickListener(this);
		findViewById(R.id.login_register).setOnClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("用户登录");
		hideBackButton();
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_btn:
			login();
			break;

		case R.id.login_register:
			break;

		default:
			break;
		}
	}

	/**
	 * 用户登录
	 */
	private void login() {
		// TODO Auto-generated method stub
		String userName = mEtUserName.getText().toString();
		LogUtil.log("login userName：" + userName);
		if (checkUserInfo(userName)) {
			String url = AppConstants.BASE_SERVER_URL + AppConstants.ACTION_USER_LOGIN + "?" + "phone=" + userName;
			doLogin(url);
		}
	}

	/**
	 * 用户登录-执行
	 */
	private void doLogin(String url) {
		LogUtil.log("doLogin：" + url);

		OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				ToastUtil.show(LoginActivity.this, "网络错误");
			}

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				UserInfo userInfo = JSON.parseObject(response, UserInfo.class);

				LogUtil.log("response:" + response);
				LogUtil.log("userInfo:" + userInfo.toString());
				if (userInfo.getResult() == 0) {
					ToastUtil.show(LoginActivity.this, "登录成功");
				} else {
					ToastUtil.show(LoginActivity.this, "登录失败");
				}
			}
		});
	}

	/**
	 * 检测用户信息
	 * 
	 * @param userName
	 * @return
	 */
	private boolean checkUserInfo(String userName) {
		if (TextUtils.isEmpty(userName)) {
			ToastUtil.show(this, "请输入正确的用户名");
			return false;
		}

		return true;
	}

}
