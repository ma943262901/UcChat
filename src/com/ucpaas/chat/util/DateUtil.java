package com.ucpaas.chat.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * 时间转换类
 * 
 * @author
 * 
 */
public class DateUtil {

	// yyyy-MM-dd EEE

	public static final String YYYYMMDD = "yyyy-MM-dd";

	public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String MMDD_HHMM = "MM-dd HH:mm";

	public static final String MMDD = "MM-dd";

	public static final String HHMM = "HH:mm";

	/**
	 * 将date转换成string
	 */
	public static String dateToStringFormat(Date date, String params) {
		SimpleDateFormat format = new SimpleDateFormat(params);
		return format.format(date);
	}

	/**
	 * 字符串获取对应的date
	 * 
	 * @param paramString
	 * @return
	 */
	public static Date stringToDateFormat(String paramString, String params) {
		try {
			// SimpleDateFormat localDate = new SimpleDateFormat(YYYYMMDD);
			SimpleDateFormat localDate = new SimpleDateFormat(params);
			return localDate.parse(paramString);
		} catch (Exception localException) {
			Log.e("localException", localException.getMessage());
		}
		return null;
	}

	/**
	 * 根据当前日期获取这一周的日期集合
	 */
	public static List<Date> dateToWeekList(Date date) {
		int day = date.getDay();
		Date fdate;
		List<Date> list = new ArrayList<Date>();
		Long fTime = date.getTime() - day * 24 * 3600000;
		for (int a = 1; a <= 7; a++) {
			fdate = new Date();
			fdate.setTime(fTime + (a * 24 * 3600000));
			list.add(a - 1, fdate);
		}
		return list;
	}

	public static List<Date> dateToWeekList2(Date date) {
		List<Date> listDate = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek < 0) {
			dayOfWeek = 0;
		}

		if (0 == dayOfWeek) {
			calendar.add(Calendar.DAY_OF_MONTH, -7);
		} else {
			calendar.add(Calendar.DAY_OF_MONTH, -dayOfWeek);
		}
		for (int i = 0, len = 7; i < len; i++) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			listDate.add(i, calendar.getTime());
		}

		return listDate;
	}

	public static String week(Date date) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		String week = "星期一";
		switch (dayOfWeek) {
		case 1:
			week = "星期日";
			break;
		case 2:
			week = "星期一";
			break;
		case 3:
			week = "星期二";
			break;
		case 4:
			week = "星期三";
			break;
		case 5:
			week = "星期四";
			break;
		case 6:
			week = "星期五";
			break;
		case 7:
			week = "星期六";
			break;

		}
		return week;
	}

	/**
	 * 根据当前日期字符串获得这一周的日期
	 * 
	 * @param args
	 */
	public static List<Date> stringToWeekList(String paramString) {

		return dateToWeekList2(stringToDateFormat(paramString, YYYYMMDD));
	}

	/**
	 * YYYYMMDD转化为MMDD
	 * 
	 * @param args
	 */
	public static String stringDateFormat(String paramString) {
		return paramString.substring(5);
	}

	/**
	 * 截取日期中的年
	 * 
	 * @param paramString
	 * @return
	 */
	public static String subYearString(String paramString) {
		return paramString.substring(0, 5);
	}

	/**
	 * 
	 * @return (mm-dd)
	 */
	public static String subMounthDay(String name, String paramString) {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append("-(").append(stringDateFormat(paramString))
				.append(")");
		return sb.toString();
	}

	/**
	 * 根据当前日期获取本周、前七天
	 * 
	 */
	public static List<Date> sevenDays() {
		List<Date> listDate = new ArrayList<Date>();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(new Date(System.currentTimeMillis()));
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

		for (int i = 7, len = 0; i > len; i--) {
			// 日期的DATE减去10 就是往后推10 天 同理 +10 就是往后推十天
			c = Calendar.getInstance(Locale.CHINA);
			c.add(Calendar.DATE, -i);
			listDate.add(c.getTime());
		}
		for (int i = 0, len = 8 - dayOfWeek; i < len; i++) {
			c = Calendar.getInstance(Locale.CHINA);
			c.add(Calendar.DATE, i);
			listDate.add(c.getTime());
		}

		return listDate;

	}

	public static void main(String[] args) {
		String time = "2013-08-02";
		Date timeDate = stringToDateFormat(time, YYYYMMDD);
		// System.out.println(dateToStringFormat(timeDate,MMDD));

		// // 定义输出日期格式
		// SimpleDateFormat sdf = new SimpleDateFormat("MMDD");
		//
		// Date currentDate = new Date();
		//
		List<Date> days = dateToWeekList(timeDate);
		for (Date date : days) {
			System.out.println(dateToStringFormat(date, MMDD));
		}
	}

	public static String format(String inFormat, long time) {
		return DateFormat.format(inFormat, time).toString();
	}

	public static String formatTime(String inFormat, long time) {
		SimpleDateFormat formatter = new SimpleDateFormat(inFormat);
		String hms = formatter.format(time);
		return hms;
	}

	/**
	 * 时间转换（ms->hh:mm:ss）
	 * 
	 * @param time
	 *            (ms)
	 * @return hh:mm:ss or mm:ss
	 */
	public static String generateTime(int time) {
		int totalSeconds = (int) (time / 1000);

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		if (hours > 0) {
			return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
					seconds).toString();
		} else {
			return String.format(Locale.US, "%02d:%02d", minutes, seconds)
					.toString();
		}
	}

	/**
	 * 时间转换（ms->hh:mm:ss）
	 * 
	 * @param time
	 *            (ms)
	 * @return hh:mm:ss or mm:ss
	 */
	public static String generateTime1(int totalSeconds) {
		// int totalSeconds = (int) (time / 1000);

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		if (hours > 0) {
			return hours + "小时" + minutes + "分" + seconds + "秒";
		} else if (minutes > 0) {
			return minutes + "分" + seconds + "秒";
		} else {
			return seconds + "秒";
		}
	}

	/**
	 * 获取当前日期的最后时间
	 * 
	 * @param date
	 *            YYYY-MM-DD hh:mm:ss
	 * @return
	 */
	public static String getDateLastTime(String date) {

		try {
			String ymdString = date.substring(0, 10);
			String hmsString = "23:59:59";
			String dateString = ymdString + " " + hmsString;
			return dateString;
		} catch (Exception e) {
			return null;
		}
	}
}
