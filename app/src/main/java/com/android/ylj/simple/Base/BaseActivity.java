package com.android.ylj.simple.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fm.commons.base.AbstractActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yulijun on 2016/12/8.
 */

public class BaseActivity extends AbstractActivity {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void exit(){
        System.exit(0);
    }
}
