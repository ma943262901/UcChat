package com.ucpaas.chat.activity;

import java.io.IOException;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.config.AppConstants;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.ToastUtil;

/**
 * 登录界面
 * 
 * @author tangqi
 * @date 2015年8月31日下午10:06:39
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText login_admin;
	private EditText login_pwd;
	private OkHttpClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle("登录界面");

		initData();
		initView();
	}

	private void initData() {
		// TODO Auto-generated method stub
		client = new OkHttpClient();
	}

	private void initView() {
		// TODO Auto-generated method stub
		login_admin = (EditText) findViewById(R.id.login_admin);
		login_pwd = (EditText) findViewById(R.id.login_pwd);

		findViewById(R.id.login_btn).setOnClickListener(this);
		findViewById(R.id.login_register).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
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
		String userName = login_admin.getText().toString();
		LogUtil.log("login userName：" + userName);
		if (checkUserInfo(userName)) {
			String url = AppConstants.BASE_SERVER_URL + AppConstants.ACTION_USER_LOGIN + "?" + "phone=" + userName;
			doLogin(url);
		}
	}

	private void doLogin(String url) {
		Request request = new Request.Builder().url(url).build();
		LogUtil.log("doLogin：" + url);

		Call call = client.newCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response arg0) throws IOException {
				// TODO Auto-generated method stub
				String response = arg0.body().string();
				UserInfo userInfo = JSON.parseObject(response, UserInfo.class);

				LogUtil.log("response:" + response);
				LogUtil.log("userInfo:" + userInfo.toString());
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				// TODO Auto-generated method stub
				LogUtil.log("onFailure");
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
