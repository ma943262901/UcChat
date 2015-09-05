/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package com.ucpaas.chat.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;

/**
 * Entity基类
 * 
 * @author Frank
 * @date 2015年9月2日上午10:20:54
 */

public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -4841630499179411834L;
	
	@Column(column = "id")
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
