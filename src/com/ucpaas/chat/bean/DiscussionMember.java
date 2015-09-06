/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package com.ucpaas.chat.bean;

/**
 * 讨论组成员
 * 
 * @author Frank
 * @date 2015年9月6日下午5:02:06
 */

public class DiscussionMember extends BaseEntity {

	private static final long serialVersionUID = 5496037714136695787L;
	private String userId;
	private String userName;
	private String imageUrl;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
