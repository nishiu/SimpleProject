package com.fm.commons.http;

import android.content.Context;

public class ContextHolder {

	private static Context host;
	
	public static Context get(){
		return host;
	}
	
	public static void set(Context ct){
		host = ct;
	}
	
}
