package com.android.ylj.simple.logic;

import com.fm.commons.http.ContextHolder;
import com.fm.commons.util.ToastUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yulijun on 2016/12/13.
 */

public class RxJavaSimpleManager {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * RxJava 订阅示例
     * @param args
     */
    public void subscriber(String... args){
        Observable.just(args)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        ToastUtils.showShortToast(ContextHolder.get(),"start prepared");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<String[], Observable<? super String>>() {
                    @Override
                    public Observable<String> call(String[] strings) {
                        return Observable.from(strings);
                    }
                })
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onNext(Object s) {
                        ToastUtils.showShortToast(ContextHolder.get(),s.toString());
                    }

                    @Override
                    public void onCompleted() {
                        ToastUtils.showShortToast(ContextHolder.get(),"completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShortToast(ContextHolder.get(),"OnError");
                        logger.error("OnError :　",e);
                    }
                });
    }

    /**
     * RxJava subscribeOn作用域范围：从下到上影响doOnsubscribe和Observable.create
     */
    public void subscribeOn(){
        Observable.just("subscribeOn")
                .subscribeOn(getSchedulerName("first thread scheduler"))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        threadInfo("doOnSubscribe one");
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        threadInfo("doOnSubscribe two");
                    }
                })
                .subscribeOn(getSchedulerName("second thread scheduler"))
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        threadInfo("doOnSubscribe three");
                    }
                })
                .subscribeOn(getSchedulerName("main thread scheduler"))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        threadInfo("onNext");
                        logger.info(s+" ->finished");
                    }
                });
    }

    /**
     * RxJava obServeOn作用域范围：从上到下，直到下一个obServeOn结束
     */
    public void obServeOn(){
        Observable.just("obServeOn")
                .observeOn(getSchedulerName("first thread scheduler"))
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        threadInfo("map 1");
                        return s+" -> map 1";
                    }
                })
                .observeOn(getSchedulerName("second thread scheduler"))
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        threadInfo("map 2");
                        return s+" -> map 2";
                    }
                })
                .observeOn(getSchedulerName("main thread scheduler"))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        threadInfo("subscribe on next");
                        logger.info(s);
                    }
                });
    }

    /**
     * RxJava obServeOnAndSubscribeOn 混合使用
     * 二者互不影响，但是SubscribeOn只会在第一次生效
     */
    public void obServeOnAndSubscribeOn(){
        Observable.just("obServeOnAndSubscribeOn")
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        threadInfo("doOnSubscribe 1");
                    }
                })
                .observeOn(getSchedulerName("observeOn one"))
                .subscribeOn(getSchedulerName("subscribeOn one"))
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        threadInfo("map 1");
                        return s+" -> map";
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        threadInfo("doOnSubscribe 2");
                    }
                })
                .observeOn(getSchedulerName("observeOn two"))
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        threadInfo("flatMap 1");
                        return Observable.just(s+" -> flatMap");
                    }
                })
                .subscribeOn(getSchedulerName("subscribeOn two"))
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        ToastUtils.showShortToast(ContextHolder.get(),"before finished toast");
                        return s;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        threadInfo("onNext");
                        logger.info(s);
                    }
                });
    }

    public void timer(){
        Observable.timer(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        logger.info("finished");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        logger.info("timer : "+aLong);
                    }
                });
    }

    public Scheduler getSchedulerName(final String name){
        return Schedulers.from(Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(name);
                return thread;
            }
        }));
    }

    public void threadInfo(String caller){
        logger.info(caller+" => "+Thread.currentThread().getName());
    }

}
