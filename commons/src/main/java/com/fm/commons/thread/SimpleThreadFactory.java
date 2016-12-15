package com.fm.commons.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleThreadFactory implements ThreadFactory {

	private static Map<String, AtomicInteger> counterMap;

	private String className;
	private AtomicInteger counter;
	private int priority;
	private boolean isDaemon;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public SimpleThreadFactory(Class<?> clazz){
		this(clazz.getSimpleName());
	}
	
	public SimpleThreadFactory(String name){
		this.className = name;
		priority = Thread.NORM_PRIORITY;
		isDaemon = false;
		initCounter();
	}
	
	private void initCounter(){
		 if(counterMap==null){
			 counterMap = new HashMap<String, AtomicInteger>();
		 }
		 counter = counterMap.get(className);
		 if(counter==null){
			 counter = new AtomicInteger(0);
			 counterMap.put(className, counter);
		 }
	}
	
	@Override
	public Thread newThread(final Runnable target) {
		String threadName = className+"["+counter.getAndIncrement()+"]";
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					target.run();
				}
				catch (Exception e) {
					logger.error("execute thread error", e);
				}
			}
		}, threadName);
		thread.setPriority(priority);
		thread.setDaemon(isDaemon);
		return thread;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isDaemon() {
		return isDaemon;
	}

	public void setDaemon(boolean isDaemon) {
		this.isDaemon = isDaemon;
	}

	public String getClassName() {
		return className;
	}

	public int getCount() {
		return counter.intValue();
	}

}
