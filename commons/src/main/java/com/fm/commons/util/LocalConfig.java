package com.fm.commons.util;

import com.fm.commons.http.ContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

public class LocalConfig {

	private static Logger logger = LoggerFactory.getLogger(LocalConfig.class);

	private static Properties properties;
	static{
		load("config.properties");
		logger.info("load properties : "+properties);
	}
	
	public static Collection<Object> keys(){
		return properties.keySet();
	}
	
	public static String get(String name){
		String value = properties.getProperty(name);
		if(value!=null&&value.intern().isEmpty()) return null;
		return value;
	}
	
	public static boolean get(String name, boolean defaultValue){
		String value = properties.getProperty(name);
		if(value==null) return defaultValue;
		return Boolean.parseBoolean(value);
	}
	
	public static long get(String name, long defaultValue){
		String value = properties.getProperty(name);
		if(value==null) return defaultValue;
		return Long.parseLong(value);
	}
	
	public static int get(String name, int defaultValue){
		String value = properties.getProperty(name);
		if(value==null) return defaultValue;
		return Integer.parseInt(value);
	}
	
	public static String get(String name, String defaultValue){
		return properties.getProperty(name, defaultValue);
	}
	
	public static void load(String path){
		try {
			if(properties==null) properties = new Properties();
			//InputStream input = LocalConfig.class.getClassLoader().getResourceAsStream(path);
			InputStream input = ContextHolder.get().getAssets().open(path);
			if(input!=null) properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
