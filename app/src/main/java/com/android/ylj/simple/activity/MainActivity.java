package com.android.ylj.simple.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.android.ylj.simple.Base.BaseActivity;
import com.android.ylj.simple.R;
import com.android.ylj.simple.adapter.SimpleAdapter;
import com.android.ylj.simple.logic.ActivityBindManager;
import com.fm.commons.logic.BeanFactory;

public class MainActivity extends BaseActivity {

    private ActivityBindManager simpleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleManager = BeanFactory.getBean(ActivityBindManager.class);
        initUI();
    }

    private void initUI(){
        ListView listView = (ListView)findViewById(R.id.simple_list);
        SimpleAdapter adapter = new SimpleAdapter(this);
        listView.setAdapter(adapter);
        adapter.setItems(simpleManager.getClassList());
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}
