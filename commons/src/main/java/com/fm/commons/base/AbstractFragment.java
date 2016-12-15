package com.fm.commons.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.fm.commons.event.ActionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by yulijun on 2016/12/1.
 */

public class AbstractFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus();
    }

    @Override
    public void onDestroy() {
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
