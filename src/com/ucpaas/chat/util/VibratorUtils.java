/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package com.ucpaas.chat.util;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * 震动工具类
 * 
 * @author Frank
 * @date 2015年9月15日下午3:27:41
 */

public class VibratorUtils {
	public final static long VIBRATE_TIME = 100;

	/**
	 * final Activity activity ：调用该方法的Activity实例 long milliseconds ：震动的时长，单位是毫秒
	 * long[] pattern ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
	 * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
	 */

	public static void vibrate(final Activity activity) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(new long[] { 100, 10, 100, 100 }, -1);
	}

	public static void vibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	public static void vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}
}
