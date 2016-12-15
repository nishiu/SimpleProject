package com.fm.commons.thread;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Handler;

public class UIThread {

	
	private static Handler handler;
	private static Map<Runnable, Runnable> warppers;
	
	//private static Logger logger = LoggerFactory.getLogger(UIThread.class);
	
	public static void init(){
		handler = new Handler();
		warppers = new HashMap<Runnable, Runnable>();
	}
	
	public static void post(Runnable runnable){
		cancel(runnable);
		handler.post(getWrapper(runnable));
	}
	
	public static void postDelayed(Runnable runnable, long ms){
		cancel(runnable);
		handler.postDelayed(getWrapper(runnable), ms);
	}
	
	public static void cancel(Runnable runnable){
		handler.removeCallbacks(getWrapper(runnable));
	}
	
	private static Runnable getWrapper(final Runnable runnable){
		Runnable wrapper = warppers.get(runnable);
		if(wrapper==null){
			wrapper = new Runnable() {				
				@Override
				public void run() {
					try {
						runnable.run();						
					}
					catch (Exception e) {
						//logger.error("execute ui runnable error", e);
					}
					finally{
						warppers.remove(runnable);
					}
				}
			};
			warppers.put(runnable, wrapper);
		}
		return wrapper;
	}
}
