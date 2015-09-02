package com.ucpaas.chat.support;

import java.util.HashMap;

import com.ucpaas.chat.config.AppConstants;
import com.ucpaas.chat.util.OkHttpClientManager;
import com.ucpaas.chat.util.StringUtils;

/**
 * 请求工厂类
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
	 * 获取请求URL
	 * 
	 * @param action
	 * @param params
	 * @return
	 */
	private String getRequestUrl(String action, HashMap<String, String> params) {
		String baseUrl = AppConstants.BASE_SERVER_URL + action;
		return StringUtils.getUrl(baseUrl, params);
	}

	/**
	 * 获取注册请求
	 * 
	 * @param userName
	 * @param nickName
	 * @return
	 */
	public String getUserRegister(String userName, String nickName) {
		String action = AppConstants.ACTION_USER_REG;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", userName);
		params.put("nickname", nickName);

		String url = getRequestUrl(action, params);
		return url;

	}

	/**
	 * 获取登录请求
	 * 
	 * @param userName
	 * @return
	 */
	public String getUserLogin(String userName) {
		String action = AppConstants.ACTION_USER_LOGIN;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", userName);

		String url = getRequestUrl(action, params);
		return url;

	}
}
