package com.ucpaas.chat.db;

import java.util.HashMap;

import com.ucpaas.chat.config.AppConstants;
import com.ucpaas.chat.util.OkHttpClientManager;
import com.ucpaas.chat.util.StringUtils;

/**
 * 请求工厂
 * 
 * @author tangqi
 * @date 2015年9月1日下午11:11:36
 */

public class RequestFactory {

	private static RequestFactory mInstance;

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static RequestFactory getInstance() {
		if (mInstance == null) {
			synchronized (OkHttpClientManager.class) {
				if (mInstance == null) {
					mInstance = new RequestFactory();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 获取注册请求
	 * 
	 * @param userName
	 * @param nickName
	 * @return
	 */
	public String getUserRegister(String userName, String nickName) {
		String baseUrl = AppConstants.BASE_SERVER_URL + AppConstants.ACTION_USER_REG;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", userName);
		params.put("nickname", nickName);

		String url = StringUtils.getUrl(baseUrl, params);
		return url;

	}

	/**
	 * 获取登录请求
	 * 
	 * @param userName
	 * @return
	 */
	public String getUserLogin(String userName) {
		String baseUrl = AppConstants.BASE_SERVER_URL + AppConstants.ACTION_USER_LOGIN;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", userName);

		String url = StringUtils.getUrl(baseUrl, params);
		return url;
	}
}
