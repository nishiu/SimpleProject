package com.fm.commons.util;

import java.math.BigDecimal;

/**
 * 1.数字格式化输出
 * 2.数字保留N位小数
 * @author L
 *
 */
public class NumberUtils {
	
	public static double format(double src,int newScale){
		BigDecimal bigDecimal = new BigDecimal(src);
		return bigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static String doubleToString(double src,int newScale){
		return String.format("%." + newScale +"f",src);
	}
	
	public static double formatTwoScale(double src){
		return format(src,2);
	}
	
	public static String doubleToStringTwoScale(double src){
		return doubleToString(src,2);
	}
	
}
