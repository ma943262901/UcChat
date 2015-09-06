package com.ucpaas.chat.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ucpaas.chat.bean.GroupInfo;
import com.ucpaas.chat.bean.UserInfo;

/**
 * JSON相关工具
 * 
 * @author tangqi
 * @date 2015年9月2日上午12:01:59
 */

public class JSONUtils {

	/**
	 * 解析JSON为对象
	 * 
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> T parseObject(String text, Class<T> clazz) {
		try {
			return JSON.parseObject(text, clazz);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析群组列表
	 * 
	 * @param paramString
	 * @return
	 */
	public static List<GroupInfo> parseGroupInfo(String paramString) {
		List<GroupInfo> groupInfoList = new ArrayList<GroupInfo>();
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		try {
			JSONArray groupJsonArray = JSONArray.parseArray(paramString);
			JSONArray userJsonArray;
			for (int i = 0; i < groupJsonArray.size(); ++i) {
				GroupInfo groupInfo = new GroupInfo();
				JSONObject groupJsonObject = (JSONObject) groupJsonArray.get(i);
				groupInfo.setGroupicon(groupJsonObject.getString("groupicon"));
				groupInfo.setGroupID(groupJsonObject.getString("groupid"));
				groupInfo.setGroupName(groupJsonObject.getString("groupname"));
				userJsonArray = groupJsonObject.getJSONArray("userlist");

				if (userJsonArray != null) {
					for (int j = 0; j < userJsonArray.size(); j++) {
						UserInfo userInfo = new UserInfo();
						JSONObject userJsonObject = (JSONObject) userJsonArray.get(i);
						userInfo.setNickname(userJsonObject.getString("nickname"));
						userInfo.setPhone(userJsonObject.getString("phone"));
						userInfo.setPortraituri(userJsonObject.getString("portraituri"));
						userInfo.setUserid(userJsonObject.getString("userid"));
						userInfoList.add(userInfo);
					}
				}
				groupInfo.setUserLists(userInfoList);
				groupInfoList.add(groupInfo);
			}
		} catch (Exception localException) {
			LogUtil.log("解析群组信息JSON失败!!!!");
		}
		return groupInfoList;
	}
}
