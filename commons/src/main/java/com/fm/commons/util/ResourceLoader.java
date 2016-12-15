package com.fm.commons.util;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fm.commons.http.HttpPoster;

public class ResourceLoader{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public boolean exists(String path){
		InputStream input = doLoad(path);
		if(input==null) return false;
		try {
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	public InputStream load(String path){
		return doLoad(path);
	}
	
	private InputStream doLoad(String path){
		InputStream input = HttpPoster.post(path);
		if(input!=null&&logger.isDebugEnabled()){
			logger.debug("load resource form apk assets : "+path);
		}
		return input;
	}
}
