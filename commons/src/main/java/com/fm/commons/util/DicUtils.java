package com.fm.commons.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 自定工具类
 * @author L
 *
 */
public class DicUtils {

	public static JSONObject buildJson(String[] keies, Object[] values) {
		try {
			JSONObject json = new JSONObject();
			if (null != keies && keies.length > 0) {
				for (int i = 0, l = keies.length; i < l; i++) {
					json.put(keies[i], values[i]);
				}
			}
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String,Object> buildParamMap(String[] keies, Object[] values) {
		Map<String,Object> map = new HashMap<String,Object>();
		if (null != keies && keies.length > 0) {
			for (int i = 0, l = keies.length; i < l; i++) {
				map.put(keies[i], values[i]);
			}
		}
		return map;
	}

}
