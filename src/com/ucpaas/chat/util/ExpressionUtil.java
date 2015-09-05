package com.ucpaas.chat.util;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.ucpaas.chat.R;

/**
 * 标签工具
 * 
 * @author msquirrel
 * 
 */
public class ExpressionUtil {
	private static ExpressionUtil intense;

	public static ExpressionUtil getIntense() {
		if (intense == null)
			intense = new ExpressionUtil();
		return intense;
	}

	private ExpressionUtil() {
	}

	/**
	 * 本地表情的资源ID
	 */
	public static int[] expressionImgs = new int[] { R.drawable.smiley_0,
			R.drawable.smiley_1, R.drawable.smiley_2, R.drawable.smiley_3, R.drawable.smiley_4,
			R.drawable.smiley_5, R.drawable.smiley_6, R.drawable.smiley_7, R.drawable.smiley_8,
			R.drawable.smiley_9, R.drawable.smiley_10, R.drawable.smiley_11, R.drawable.smiley_12,
			R.drawable.smiley_13, R.drawable.smiley_14, R.drawable.smiley_15, R.drawable.smiley_16,
			R.drawable.smiley_17, R.drawable.smiley_18, R.drawable.smiley_19, R.drawable.smiley_20,
			R.drawable.smiley_21, R.drawable.smiley_22, R.drawable.smiley_23 };

	/**
	 * 本地表情的名
	 */
	public static String[] expressionImgNames = new String[] { "[f000]",
			"[f001]", "[f002]", "[f003]", "[f004]", "[f005]", "[f006]",
			"[f007]", "[f008]", "[f009]", "[f010]", "[f011]", "[f012]",
			"[f013]", "[f014]", "[f015]", "[f016]", "[f017]", "[f018]",
			"[f019]", "[f020]", "[f021]", "[f022]", "[f023]" };

	public static int[] expressionImgs1 = new int[] { R.drawable.smiley_24,
			R.drawable.smiley_25, R.drawable.smiley_26, R.drawable.smiley_27, R.drawable.smiley_28,
			R.drawable.smiley_29, R.drawable.smiley_30, R.drawable.smiley_31, R.drawable.smiley_32,
			R.drawable.smiley_33, R.drawable.smiley_34, R.drawable.smiley_35, R.drawable.smiley_36,
			R.drawable.smiley_37, R.drawable.smiley_38, R.drawable.smiley_39, R.drawable.smiley_40,
			R.drawable.smiley_41, R.drawable.smiley_42, R.drawable.smiley_43, R.drawable.smiley_44,
			R.drawable.smiley_45, R.drawable.smiley_46, R.drawable.smiley_47 };

	/**
	 * 本地表情的名
	 */
	public static String[] expressionImgNames1 = new String[] { "[f024]",
			"[f025]", "[f026]", "[f027]", "[f028]", "[f029]", "[f030]",
			"[f031]", "[f032]", "[f033]", "[f034]", "[f035]", "[f036]",
			"[f037]", "[f038]", "[f039]", "[f040]", "[f041]", "[f042]",
			"[f043]", "[f044]", "[f045]", "[f046]", "[f047]" };

	public static int[] expressionImgs2 = new int[] { R.drawable.smiley_48,
			R.drawable.smiley_49, R.drawable.smiley_50, R.drawable.smiley_51, R.drawable.smiley_52,
			R.drawable.smiley_53, R.drawable.smiley_54, R.drawable.smiley_55, R.drawable.smiley_56,
			R.drawable.smiley_57, R.drawable.smiley_58, R.drawable.smiley_59, R.drawable.smiley_60,
			R.drawable.smiley_61, R.drawable.smiley_62, R.drawable.smiley_63, R.drawable.smiley_64,
			R.drawable.smiley_65, R.drawable.smiley_66, R.drawable.smiley_67, R.drawable.smiley_68,
			R.drawable.smiley_69, R.drawable.smiley_70, R.drawable.smiley_71 };

	/**
	 * 本地表情的名
	 */
	public static String[] expressionImgNames2 = new String[] { "[f048]",
			"[f049]", "[f050]", "[f051]", "[f052]", "[f053]", "[f054]",
			"[f055]", "[f056]", "[f057]", "[f058]", "[f059]", "[f060]",
			"[f061]", "[f062]", "[f063]", "[f064]", "[f065]", "[f066]",
			"[f067]", "[f068]", "[f069]", "[f070]", "[f071]" };

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 */
	private void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			if (matcher.start() < start) {
				continue;
			}
			Field field = R.drawable.class.getDeclaredField(key);
			int resId = Integer.parseInt(field.get(null).toString());
			if (resId != 0) {
				Bitmap bitmap = BitmapFactory.decodeResource(
						context.getResources(), resId);
				ImageSpan imageSpan = new ImageSpan(bitmap);
				int end = matcher.start() + key.length();
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				if (end < spannableString.length()) {
					dealExpression(context, spannableString, patten, end);
				}
				break;
			}
		}
	}

	/**
	 * 判断传入str里是否有图片，有图片就表情图片代�? *
	 * 
	 * @param context上下文环
	 *            �? * @param str传入的字符串
	 * @param zhengze正则表达
	 *            �? * @return
	 */
	public SpannableString getExpressionString(Context context, String str,
			String zhengze) {
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
		}
		return spannableString;
	}
}