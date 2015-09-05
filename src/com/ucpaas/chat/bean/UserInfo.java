package com.ucpaas.chat.bean;

import com.lidroid.xutils.db.annotation.Column;

/**
 * 用户信息
 * 
 * @author tangqi
 * @date 2015年8月31日下午11:43:12
 */

public class UserInfo extends BaseEntity{
	
	private static final long serialVersionUID = -4720419100349025078L;
	
	@Column(column = "result")
	private String result;
	
	@Column(column = "phone")
	private String phone;
	
	@Column(column = "imtoken")
	private String imtoken;
	
	@Column(column = "nickname")
	private String nickname;
	
	@Column(column = "portraituri")
	private String portraituri;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImtoken() {
		return imtoken;
	}

	public void setImtoken(String imtoken) {
		this.imtoken = imtoken;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPortraituri() {
		return portraituri;
	}

	public void setPortraituri(String portraituri) {
		this.portraituri = portraituri;
	}

	@Override
	public String toString() {
		return "UserInfo [result=" + result + ", phone=" + phone + ", imtoken=" + imtoken + ", nickname=" + nickname + ", portraituri=" + portraituri
				+ "]";
	}

}
