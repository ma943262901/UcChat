package com.ucpaas.chat.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 字符串
 * 
 * @author tangqi
 * @date 2015年9月1日下午10:32:23
 */

public class StringUtils {

	/**
	 * 拼接Get请求URL
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String getUrl(String url, HashMap<String, String> params) {
		if (params != null) {
			Iterator<String> it = params.keySet().iterator();
			StringBuffer sb = null;
			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);
				if (sb == null) {
					sb = new StringBuffer();
					sb.append("?");
				} else {
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
			url += sb.toString();
		}
		return url;
	}
}
