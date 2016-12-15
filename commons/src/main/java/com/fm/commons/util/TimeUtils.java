package com.fm.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by admin on 2016/7/15.
 */
public class TimeUtils {
	
	/**
	 * 计算两个时间字符串之间的时间差
	 * @param early		相对靠前的时间字符串
	 * @param late		相对靠后的时间字符串
	 * @param format	输入的两个时间约定的格式
	 * @return			适当精度的字符串结果
	 */
	public static String timeDifference(String early, String late, SimpleDateFormat format) {
		Date d1 = null;
		Date d2 = null;
		String result = "";

		try {
			d1 = format.parse(early);
			d2 = format.parse(late);

			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000;
			long diffMinutes, diffHours, diffDays;
			if (diffSeconds > 59) {
				diffSeconds = diff / 1000 % 60;
				diffMinutes = diff / (60 * 1000);
				if (diffMinutes > 59) {
					diffMinutes = diff / (60 * 1000) % 60;
					diffHours = diff / (60 * 60 * 1000);
					if (diffHours > 23) {
						diffHours = diff / (60 * 60 * 1000) % 24;
						diffDays = diff / (24 * 60 * 60 * 1000);
						result = diffDays + " 天 " + diffHours + " 小时";
					} else {
						diffMinutes++;
						result = diffHours + " 小时 " + diffMinutes + " 分钟";
					}
				} else {
					result = diffMinutes + " 分钟 " + diffSeconds + " 秒";
				}
			} else {
				result = diffSeconds + " 秒";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 求当前月第一天是星期几并转换成对应的TextView数组偏移量
	 * 
	 * @return 起始下标
	 */
	public static int firstDayInMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return dayOfWeek;
	}

	/**
	 * 计算给定月份的自然天数
	 * 
	 * @return
	 */
	public static int dayOfMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	@SuppressWarnings("deprecation")
	public static String getMoreReadableTime(String time) {
		String res = null;
		try {
			boolean showYear = false;
			boolean showMonth = false;
			String dayString = null;

			Calendar cal = Calendar.getInstance();
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
			cal.setTime(date);

			if (cal.get(Calendar.YEAR) - 1900 - new Date().getYear() != 0)
				showYear = true;

			if (cal.get(Calendar.MONTH) - new Date().getMonth() != 0) {
				showMonth = true;
				switch (new Date().getDate() - cal.get(Calendar.DATE)) {
				case 0:
					dayString = "今天";
					break;
				case 1:
					dayString = "昨天";
					break;
				case 2:
					dayString = "前天";
					break;
				}
			}
			
			if (!showYear) {
				if (dayString != null && !showMonth)
					res = dayString + "\n" + time.substring(11, 16);
				else
					res = new SimpleDateFormat("M月d日").format(date) + "\n" + time.substring(11, 16);
			} else
				res = new SimpleDateFormat("y年M月d日").format(date) + "\n" + time.substring(11, 16);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;

	}

	/**
	 * 本地化时间字符串
	 * 
	 * @param time
	 *            输入格式yyyy-MM-dd HH:mm:ss
	 * @return 输出格式yyyy年M月d日
	 */
	public static String localizeTimeString(String time) {
		String year = time.substring(0, 4);
		int mounth = Integer.parseInt(time.substring(5, 7));
		int day = Integer.parseInt(time.substring(8, 10));
		String res = year + "年" + mounth + "月" + day + "日";
		return res;
	}
}
