package com.fm.commons.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapLoader {
	
	private int sample = 1;
	private Bitmap.Config preferredConfig = Bitmap.Config.RGB_565;

	private ResourceLoader resourceLoader;
	private BitmapFactory.Options options;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public BitmapLoader() {
		resourceLoader = new ResourceLoader();
	}
	
	public boolean exists(String path){
		return resourceLoader.exists(path);
	}
	
	public Bitmap load(String path){
		InputStream input = null;
		Bitmap bitmap = null;
		BufferedInputStream bufferedIs = null;
		ByteArrayOutputStream byteArrayOs = null;
		try{
			if(null != path && !"".equals(path)){
				input = resourceLoader.load(path);
				bufferedIs = new BufferedInputStream(input, 1024*8);
				byteArrayOs = new ByteArrayOutputStream();
				int len = 0;
				byte[] buffer = new byte[1024];
				while((len = bufferedIs.read(buffer)) != -1){
					byteArrayOs.write(buffer,0 , len);
				}
				bufferedIs.close();
				byteArrayOs.close();
	            byte[] bytes = byteArrayOs.toByteArray();
	            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, getBitmapOption());
			}
        	//logger.debug("decode bitmap : "+path);
		}catch(Exception e){
			logger.error("load bitmap error: "+ path);
		}finally{
			close(input);
		}
		return bitmap;
	}
		
	private void close(InputStream input) {
		if(input==null) return;
		try {
			input.close();
		} catch (IOException e) {
			logger.error("close bitmap input stream error", e);
		}
	}
	
	protected BitmapFactory.Options getBitmapOption(){
		if(options==null){
			options = new BitmapFactory.Options();
			options.inPreferredConfig = preferredConfig;
			options.inSampleSize = sample;
			options.inTargetDensity = 240;
		}
		return options;
	}

	public int getSample() {
		return sample;
	}

	public void setSample(int sample) {
		this.sample = sample;
	}

	public Bitmap.Config getPreferredConfig() {
		return preferredConfig;
	}

	public void setPreferredConfig(Bitmap.Config preferredConfig) {
		this.preferredConfig = preferredConfig;
	}
}
