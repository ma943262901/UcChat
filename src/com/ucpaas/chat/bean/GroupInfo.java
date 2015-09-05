package com.ucpaas.chat.bean;

import java.util.List;

/**
 *
 * @author tangqi
 * @date 2015年9月5日下午11:20:39
 */

public class GroupInfo extends BaseEntity {

	private static final long serialVersionUID = 2894183312549380195L;
	private String groupID = "";
	private String groupName = "";
	private String groupicon = "";
	private List<UserInfo> userLists;

	public String getGroupID() {
		return this.groupID;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public String getGroupicon() {
		return this.groupicon;
	}

	public List<UserInfo> getUserLists() {
		return this.userLists;
	}

	public void setGroupID(String paramString) {
		this.groupID = paramString;
	}

	public void setGroupName(String paramString) {
		this.groupName = paramString;
	}

	public void setGroupicon(String paramString) {
		this.groupicon = paramString;
	}

	public void setUserLists(List<UserInfo> paramList) {
		this.userLists = paramList;
	}
}
