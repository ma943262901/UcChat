/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package com.ucpaas.chat.support;

import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.config.SpKey;

import android.content.Context;

/**
 * SharedPreferences相关操作
 * 
 * @author Frank
 * @date 2015年9月2日上午11:13:24
 */

public class SpOperation {

	/**
	 * 保存用户信息
	 * 
	 * @param context
	 * @param userInfo
	 */
	public static void saveUserInfo(Context context, UserInfo userInfo) {
		SpHelper spHelper = SpHelper.getInstance(context);
		spHelper.put(SpKey.PHONE, userInfo.getPhone());
		spHelper.put(SpKey.TOKEN, userInfo.getImtoken());
		spHelper.put(SpKey.NICKNAME, userInfo.getNickname());
		spHelper.put(SpKey.PORTRAIT_URI, userInfo.getPortraituri());
	}

	/**
	 * 获取用户信息
	 * 
	 * @param context
	 * @return
	 */
	public static UserInfo getUserInfo(Context context) {
		SpHelper spHelper = SpHelper.getInstance(context);
		UserInfo userInfo = new UserInfo();

		userInfo.setPhone(spHelper.get(SpKey.PHONE));
		userInfo.setImtoken(spHelper.get(SpKey.TOKEN));
		userInfo.setNickname(spHelper.get(SpKey.NICKNAME));
		userInfo.setPortraituri(spHelper.get(SpKey.PORTRAIT_URI));

		return userInfo;
	}

	/**
	 * 获取Token
	 * 
	 * @param context
	 * @return
	 */
	public static String getToken(Context context) {
		return SpHelper.getInstance(context).get(SpKey.TOKEN);
	}
}
