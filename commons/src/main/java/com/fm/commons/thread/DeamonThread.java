package com.fm.commons.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认四个线程
 * @author L
 *
 */
public class DeamonThread {
	
	private static Logger logger = LoggerFactory.getLogger(DeamonThread.class);
	
	private static Executor executor;
	
	static{
		ThreadFactory tf = new SimpleThreadFactory(DeamonThread.class);
		executor = Executors.newFixedThreadPool(6, tf);
	}
	
	public static Executor getExecutor(){
		return executor;
	}
	
	public static void post(Runnable runnable){
		post(runnable, 0);
	}
	
	public static void post(final Runnable runnable, final long delay){
		executor.execute(new Runnable() {			
			@Override
			public void run() {
				try {
					if(delay>0) Thread.sleep(delay);
					runnable.run();
				} catch (Exception e) {
					logger.error("execute deamon runnable error", e);
				}
			}
		});
	}
}
