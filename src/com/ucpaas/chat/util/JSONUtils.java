package com.ucpaas.chat.util;

import com.alibaba.fastjson.JSON;

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
		return JSON.parseObject(text, clazz);
	}
}
