package com.ucpaas.chat.support;

import android.content.Context;

import com.ucpaas.chat.util.FileUtils;

/**
 * App缓存管理
 * 
 * @author tangqi
 * @data 2015年8月8日上午11:35:33
 */

public class CacheManager {

	/**
	 * 获取外部缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static String getExternalCachePath(Context context) {
		return FileUtils.getExternalCacheDir(context);
	}

	/**
	 * 获取联系人列表
	 * 
	 * @param context
	 * @return
	 */
	public static String getFriendDbPath(Context context) {
		return context.getCacheDir().getAbsolutePath();
	}
}
