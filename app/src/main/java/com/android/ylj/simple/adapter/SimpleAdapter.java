package com.android.ylj.simple.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.android.ylj.simple.Base.BaseUnit;
import com.android.ylj.simple.R;

import java.util.List;

/**
 * Created by yulijun on 2016/12/8.
 */

public class SimpleAdapter extends BaseAdapter {

    private List<BaseUnit> items;
    private Context context;

    public SimpleAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<BaseUnit> items){
        if(items == null)return;
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(items == null )return 0;
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        if(items == null)return null;
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.simple_list_item,null);
        }
        update(convertView,position);
        return convertView;
    }

    private void update(View convert,final int position){
        Button button = (Button)convert.findViewById(R.id.simple_btn);
        final BaseUnit target = items.get(position);
        button.setText(target.getTitle());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context,target.getClazz());
                context.startActivity(intent);
            }
        });
    }
}
