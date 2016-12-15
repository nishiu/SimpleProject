package com.fm.commons.util;

import java.util.concurrent.Executor;

import com.fm.commons.thread.DeamonThread;

import android.os.AsyncTask;

/**
 * AsyncTask 的线程池
 * @author L
 *
 */
public class AsyncTaskUtils {
	
	private static Executor getExecutor(){
		return DeamonThread.getExecutor();
	}
	
	public static AsyncTask<?, ?, ?> execute(AsyncTask<?, ?, ?> task){
		if(null != task) task.executeOnExecutor(getExecutor(),null);
		return task;
	}
	
	public static AsyncTask execute(AsyncTask task,String[] params){
		if(null != task) task.executeOnExecutor(getExecutor(),params);
		return task;
	}
		
	public static AsyncTask execute(AsyncTask task,Object[] params){
		if(null != task) task.executeOnExecutor(getExecutor(),params);
		return task;
	}
	
}
