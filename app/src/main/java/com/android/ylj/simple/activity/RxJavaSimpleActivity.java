package com.android.ylj.simple.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.android.ylj.simple.Base.BaseActivity;
import com.android.ylj.simple.R;
import com.android.ylj.simple.logic.RxJavaSimpleManager;
import com.fm.commons.logic.BeanFactory;

/**
 * Created by yulijun on 2016/12/8.
 */

public class RxJavaSimpleActivity extends BaseActivity {

    private RxJavaSimpleManager rxJavaSimpleManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);
        setTitle(R.string.label_rxjava_simple);
        rxJavaSimpleManager = BeanFactory.getBean(RxJavaSimpleManager.class);
        initWidget();
    }

    private void initWidget(){

        createButton(R.id.btn_internet_ip,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxJavaSimpleManager.timer();
            }
        });

        createButton(R.id.btn_subscriber,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxJavaSimpleManager.subscriber("one","two","three");
            }
        });

        createButton(R.id.btn_subscriber_error,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxJavaSimpleManager.subscriber("one",null,"three");
            }
        });

        createButton(R.id.btn_subscribeOn,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxJavaSimpleManager.subscribeOn();
            }
        });

        createButton(R.id.btn_obServeOn,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxJavaSimpleManager.obServeOn();
            }
        });

        createButton(R.id.btn_obServeOnAndSubscribeOn,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxJavaSimpleManager.obServeOnAndSubscribeOn();
            }
        });
    }

    private void createButton(int resId,View.OnClickListener clickListener){
        Button button = (Button)findViewById(resId);
        button.setOnClickListener(clickListener);
    }

}
