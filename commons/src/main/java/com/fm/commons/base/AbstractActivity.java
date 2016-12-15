package com.fm.commons.base;

import android.support.v7.app.AppCompatActivity;

import com.fm.commons.event.ActionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by yulijun on 2016/11/12.
 */

public class AbstractActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        registerEventBus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
    }

    @Subscribe
    public void onEvent(ActionEvent event){

    }

    protected void registerEventBus(){
        if(EventBus.getDefault().isRegistered(this))return;
        EventBus.getDefault().register(this);
    }

    protected void unRegisterEventBus(){
        if(!EventBus.getDefault().isRegistered(this))return;
        EventBus.getDefault().unregister(this);
    }

}
