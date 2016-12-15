package com.fm.commons.action;

import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fm.commons.thread.UIThread;
import com.fm.commons.util.AsyncTaskUtils;
import com.fm.commons.util.ToastUtils;

import android.content.Context;
import android.os.AsyncTask;

public class ActionSupport {

	//protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private Context context;
	
	public ActionSupport(WeakReference<Context> weakReference) {
		this.context = weakReference.get();
	}
	
	protected void showNotice(final CharSequence msg){
		UIThread.post(new Runnable() {
			
			@Override
			public void run() {
				ToastUtils.showShortToast(context, msg.toString());
			}
		});
	}
	
	public Context getContext(){
		return context;
	}
	
	public String getString(int resId){
		return getContext().getString(resId);
	}
	
	protected void execute(AsyncTask<?, ?, ?> task){
		AsyncTaskUtils.execute(task);
	}
	
	protected void execute(AsyncTask<?, ?, ?> task,String[] params){
		AsyncTaskUtils.execute(task, params);
	}
	
	
}
