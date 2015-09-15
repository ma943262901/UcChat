/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package com.ucpaas.chat.util;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * 铃声工具类
 * 
 * @author Frank
 * @date 2015年9月15日下午4:58:12
 */

public class RingtoneUtils {

	public static void playDefault(Context context) {
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(context, notification);
		r.play();
	}
}
