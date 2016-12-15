package com.android.ylj.simple.Base;

/**
 * Created by yulijun on 2016/12/8.
 */

public class BaseUnit {

    private Class<? extends BaseActivity> clazz;
    private String title;

    public Class<? extends BaseActivity> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends BaseActivity> clazz) {
        this.clazz = clazz;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
