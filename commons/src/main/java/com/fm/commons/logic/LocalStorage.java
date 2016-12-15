package com.fm.commons.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.fm.commons.http.ContextHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;

public class LocalStorage {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Gson gson;

	public LocalStorage() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	}

	public <T> T get(String key, Type type) {
		return get(key, type, (T) null);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Type type, T defaultValue) {
		try {
			String json = getPreferences().getString(key, null);
			if (json != null && json.length() > 0) {
				return (T) gson.fromJson(json, type);
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public <T> T get(String key, Class<T> clazz) {
		return get(key, clazz, null);
	}

	public <T> T get(String key, Class<T> clazz, T defaultValue) {
		try {
			String json = getPreferences().getString(key, null);
			if (json != null && json.length() > 0) {
				return gson.fromJson(json, clazz);
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public void put(String key, Object value) {
		if (null == value)return;
		Class<?> clazz = value.getClass();
		switch (clazz.getName()) {
		case "java.lang.Integer":
			putInt(key, (int) (value));
			break;
		case "java.lang.String":
			putString(key, ""+value);
			break;
		case "java.lang.Boolean":
			putBoolean(key,(boolean)(value));
			break;
		case "java.lang.Long":
			putLong(key,(long)(value));
			break;
		default:
			putString(key,gson.toJson(value));
			break;
		}
	}

	public void put(String[] keys, Object[] values) {
		Editor edit = getPreferences().edit();
		for (int i = 0, l = keys.length; i < l; i++) {
			put(keys[i],values[i]);
		}
		edit.commit();
	}

	public void putInt(String key, int value){
		Editor edit = getPreferences().edit();
		edit.putInt(key,value);
		edit.commit();
	}

	public void putLong(String key, long value){
		Editor edit = getPreferences().edit();
		edit.putLong(key,value);
		edit.commit();
	}

	public void putBoolean(String key, boolean value){
		Editor edit = getPreferences().edit();
		edit.putBoolean(key,value);
		edit.commit();
	}

	public void putString(String key, String value){
		Editor edit = getPreferences().edit();
		edit.putString(key,value);
		edit.commit();
	}

	public void remove(String key) {
		put(key, null);
	}

	public void remove(List<String> keys) {
		Editor edit = getPreferences().edit();
		for (String key : keys) {
			edit.putString(key, null);
		}
		edit.commit();
	}

	public void clear() {
		SharedPreferences pref = getPreferences();
		Editor edit = pref.edit();
		for (String key : pref.getAll().keySet()) {
			edit.putString(key, null);
		}
		edit.commit();
	}

	private SharedPreferences getPreferences() {
		String name = ContextHolder.get().getPackageName();
		return ContextHolder.get().getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public boolean get(String key, boolean defaultValue) {
		try{
			Boolean value = getPreferences().getBoolean(key,defaultValue);
			if(value == null)return defaultValue;
			return value.booleanValue();
		}catch (Exception e){
			e.printStackTrace();
			logger.error("LocalStorage read Boolean value error : ",e);
			return defaultValue;
		}
	}

	public String get(String key, String defaultValue) {
		return get(key, String.class, defaultValue);
	}

	public int get(String key, int defaultValue){
		try {
			Integer value = getPreferences().getInt(key, defaultValue);
			if (value == null) return defaultValue;
			return value.intValue();
		} catch (ClassCastException e) {
			e.printStackTrace();
			logger.error("LocalStorage read Integer value error : ",e);
			return defaultValue;
		}
	}

	public long get(String key, long defaultValue){
		try{
			Long value = getPreferences().getLong(key,defaultValue);
			if (value == null)return defaultValue;
			return value.longValue();
		}catch (Exception e){
			e.printStackTrace();
			logger.error("LocalStorage read Long value error : ",e);
			return defaultValue;
		}
	}
}