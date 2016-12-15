package com.android.ylj.simple.logic;

import com.android.ylj.simple.Base.BaseActivity;
import com.android.ylj.simple.Base.BaseUnit;
import com.android.ylj.simple.activity.RxJavaSimpleActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yulijun on 2016/12/8.
 */

public class ActivityBindManager {

    private List<BaseUnit> activities;

    public ActivityBindManager() {
        activities = new LinkedList<>();
        initActivity();
    }

    private void initActivity(){
        activities.add(create(RxJavaSimpleActivity.class,"RxJavaSimpleActivity"));
    }

    private BaseUnit create(Class<? extends BaseActivity> clazz, String title){
        BaseUnit unit = new BaseUnit();
        unit.setClazz(clazz);
        unit.setTitle(title);
        return unit;
    }

    public List<BaseUnit> getClassList(){
        return activities;
    }
}
