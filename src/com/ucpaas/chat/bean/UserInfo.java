package com.ucpaas.chat.bean;

/**
 * 用户信息
 * 
 * @author tangqi
 * @date 2015年8月31日下午11:43:12
 */

public class UserInfo extends BaseEntity{
	
	private static final long serialVersionUID = -4720419100349025078L;
	
	private String result;
	private String phone;
	private String imtoken;
	private String nickname;
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
