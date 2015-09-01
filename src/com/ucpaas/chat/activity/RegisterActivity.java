package com.ucpaas.chat.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Request;
import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.db.RequestFactory;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.OkHttpClientManager;
import com.ucpaas.chat.util.ToastUtil;

/**
 * 注册
 * 
 * @author tangqi
 * @date 2015年9月1日下午9:44:26
 */

public class RegisterActivity extends BaseActivity {

	private EditText mEtUserName;
	private EditText mEtUserNickName;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_register);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mEtUserName = (EditText) findViewById(R.id.et_login_admin);
		mEtUserNickName = (EditText) findViewById(R.id.login_nickname);

		TextView btnRegister = (TextView) findViewById(R.id.tv_register);
		btnRegister.setOnClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("用户注册");
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_register:
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
		String userName = mEtUserName.getText().toString();
		String nickName = mEtUserNickName.getText().toString();

		if (checkUserInfo(userName, nickName)) {
			String url = RequestFactory.getInstance().getUserRegister(userName, nickName);
			doRegister(url);
		}
	}

	/**
	 * 注册-发送数据
	 * 
	 * @param url
	 */
	private void doRegister(String url) {
		LogUtil.log("doRegister：" + url);

		OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				ToastUtil.show(RegisterActivity.this, "网络错误");
				registerFailure();
			}

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				UserInfo userInfo = JSON.parseObject(response, UserInfo.class);
				LogUtil.log("response:" + response);
				LogUtil.log("userInfo:" + userInfo.toString());

				if (userInfo.getResult() == 0) {
					registerSuccess(userInfo);
				} else {
					ToastUtil.show(RegisterActivity.this, "注册失败，请输入正确的手机号");
					registerFailure();
				}
			}
		});
	}

	/**
	 * 注册成功
	 */
	private void registerSuccess(UserInfo userInfo) {
		// TODO Auto-generated method stub
		ToastUtil.show(RegisterActivity.this, "注册成功，手机号" + userInfo.getPhone());
		finish();
	}

	/**
	 * 注册失败
	 */
	private void registerFailure() {
		// TODO Auto-generated method stub

	}

	/**
	 * 检测用户信息是否有效
	 * 
	 * @param userName
	 * @param nickName
	 * @return
	 */
	private boolean checkUserInfo(String userName, String nickName) {
		if (TextUtils.isEmpty(userName)) {
			ToastUtil.show(this, "手机号为空");
			return false;
		}

		if (TextUtils.isEmpty(nickName)) {
			ToastUtil.show(this, "昵称为空");
			return false;
		}

		return true;
	}

}
