package com.android.ylj.simple.logic;

import com.fm.commons.logic.BeanFactory;

/**
 * Created by yulijun on 2016/12/8.
 */

public class LogicModule {

    /**
     * logic manager intalization
     */
    public static void init(){
        BeanFactory.getBean(ActivityBindManager.class);
        BeanFactory.getBean(RxJavaSimpleManager.class);
    }
}
