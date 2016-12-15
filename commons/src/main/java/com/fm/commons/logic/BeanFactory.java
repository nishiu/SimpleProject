package com.fm.commons.logic;

import java.util.HashMap;
import java.util.Map;

public class BeanFactory {

	private static Map<Object, Object> beanMap = new HashMap<Object, Object>();
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz){
		Object bean = beanMap.get(clazz);
		if(bean==null){
			try {
				bean = clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("instance bean error", e);
			}
			bind(clazz, bean);
		}
		return (T)bean;
	}
	
	public static <T> void bind(Class<T> clazz, Object bean){
		beanMap.put(clazz, bean);
	}
}
