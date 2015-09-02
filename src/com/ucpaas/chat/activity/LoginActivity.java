package com.ucpaas.chat.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.config.ResultCode;
import com.ucpaas.chat.support.RequestFactory;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.util.JSONUtils;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.OkHttpClientManager;
import com.ucpaas.chat.util.ToastUtil;
import com.ucpaas.chat.util.VersionUtil;

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
	private TextView mTvAppVersion;
	private long mExitTime;
	private final static long TIME_DIFF = 2 * 1000;

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
		mEtUserName = (EditText) findViewById(R.id.et_login_admin);
		mEtUserPwd = (EditText) findViewById(R.id.et_login_pwd);
		mTvAppVersion = (TextView) findViewById(R.id.tv_app_version);

		TextView btnLogin = (TextView) findViewById(R.id.tv_login_login);
		ImageView ivRegister = (ImageView) findViewById(R.id.iv_login_register);
		btnLogin.setOnClickListener(this);
		ivRegister.setOnClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		mTvAppVersion.setText("当前版本V" + VersionUtil.getVersionName(this));
		setTitle("用户登录");
		hideBackButton();
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_login_login:
			login();
			break;

		case R.id.iv_login_register:
			register();
			break;

		default:
			break;
		}
	}

	/**
	 * 注册
	 */
	private void register() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	/**
	 * 用户登录
	 */
	private void login() {
		// TODO Auto-generated method stub
		String userName = mEtUserName.getText().toString();
		if (checkUserInfo(userName)) {
			String url = RequestFactory.getInstance().getUserLogin(userName);
			doLogin(url);
		}
	}

	/**
	 * 用户登录-发送数据
	 */
	private void doLogin(String url) {
		LogUtil.log("doLogin：" + url);

		OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				ToastUtil.show(LoginActivity.this, "网络错误");
				loginFailure();
			}

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				UserInfo userInfo = JSONUtils.parseObject(response, UserInfo.class);
				LogUtil.log("response:" + response);

				if (ResultCode.OK.equals(userInfo.getResult())) {
					loginSuccess(userInfo);
				} else {
					ToastUtil.show(LoginActivity.this, "手机号无效，请先注册");
					loginFailure();
				}
			}
		});
	}

	/**
	 * 登录成功
	 */
	private void loginSuccess(UserInfo userInfo) {
		// TODO Auto-generated method stub
		ToastUtil.show(LoginActivity.this, "登录成功");
		saveUserInfo(userInfo);
		
		Intent intent = new Intent(LoginActivity.this, TestApiActivity.class);
		intent.putExtra("userInfo", userInfo);
		startActivity(intent);
		finish();
	}

	/**
	 * 登录失败
	 */
	private void loginFailure() {
		// TODO Auto-generated method stub

	}

	/**
	 * 保存用户信息
	 */
	private void saveUserInfo(UserInfo userInfo) {
		SpOperation.saveUserInfo(LoginActivity.this, userInfo);
	}

	/**
	 * 检测用户信息是否有效
	 * 
	 * @param userName
	 * @return
	 */
	private boolean checkUserInfo(String userName) {
		if (TextUtils.isEmpty(userName)) {
			ToastUtil.show(this, "请输入正确的手机号");
			return false;
		}

		return true;
	}

	/**
	 * 再按一次退出程序
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > TIME_DIFF) {
				ToastUtil.show(LoginActivity.this, "再按一次退出程序");
				mExitTime = System.currentTimeMillis();
			} else {
				System.exit(0);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
