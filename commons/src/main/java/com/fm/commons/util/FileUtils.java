package com.fm.commons.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils {

	public final static String SDPATH;
	public final static String PIC_SUFFIX;
	
	static{
		PIC_SUFFIX = "_";
		SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/tm/" + DeviceInfoUtils.getPackageName() + "/";
		System.out.println("SDPATH: "+SDPATH);
	}
	
	/**	 * 
	 * @throws IOException
	 */
	public static File creatSDFile(String fileName) throws IOException {
		validateRoot();
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}
	
	private static void validateRoot(){
		File file = new File(SDPATH);
		if(!file.exists()){
			file.mkdirs();
		}
	}

	/**
	 */
	public static boolean exist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	
	public static boolean existURL(String url){
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		if(!fileName.endsWith(".apk")) fileName = fileName + FileUtils.PIC_SUFFIX; 
		File file = new File(SDPATH  + fileName);
		return file.exists();
	}
	
	public static boolean existTmpURL(String url){
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		File file = new File(SDPATH  + fileName + ".tmp");
		return file.exists();
	}

	//delete file if exists
	public static void delete(String fileName){
		File file = new File(SDPATH + fileName);
		if(null != file && file.exists()){			
			file.delete();
		}
	}
	
	public static long getFileSize(String fileName){
		File file = new File(SDPATH + fileName);
		if(!file.exists()) return 0;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			int size = fis.available();
			return size;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != fis)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return 0;
	}
}