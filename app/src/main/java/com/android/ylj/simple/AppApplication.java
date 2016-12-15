package com.android.ylj.simple;

import android.app.Application;

import com.android.ylj.simple.logic.LogicModule;
import com.fm.commons.http.ContextHolder;
import com.fm.commons.thread.UIThread;

/**
 * Created by yulijun on 2016/12/8.
 */

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.set(this);
        UIThread.init();
        LogicModule.init();
    }
}
