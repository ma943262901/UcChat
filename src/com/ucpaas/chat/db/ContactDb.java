package com.ucpaas.chat.db;

import java.util.List;

import com.ucpaas.chat.bean.UserInfo;

/**
 *
 * @author tangqi
 * @date 2015年9月5日下午6:37:03
 */

public interface ContactDb {

	/**
	 * 保存联系人
	 * 
	 * @param list
	 */
	public void insert(UserInfo userInfo);

	/**
	 * 保存一组联系人
	 * 
	 * @param userInfoList
	 */
	public void insert(List<UserInfo> userInfoList);

	/**
	 * 查找所有联系人
	 * 
	 * @param list
	 */
	public List<UserInfo> queryAll();
}
