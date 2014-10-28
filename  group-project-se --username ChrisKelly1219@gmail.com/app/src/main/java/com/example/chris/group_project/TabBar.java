package com.example.chris.group_project;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

/**
 * Created by Jonny on 10/20/14.
 */
public class TabBar extends LinearLayout implements OnClickListener {

    private TabBarAdapter adapter;

    public TabBar(Context context){
        super(context);
    }

    // To allow instantiation in xml
    public TabBar(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public TabBar(Context context, ViewGroup parent, TabBarAdapter adapter){
        super(context);
        this.adapter = adapter;

        ViewGroup tabBarView = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.tab_bar, parent, false);

        Integer count = adapter.getCount();
        for (Integer i = 0; i < count; i++){
            TabBarButton button = adapter.getTabBarButtonForIndex(i);
            button.setTag(i);
            button.setOnClickListener(this);
            tabBarView.addView(button);
        }

        parent.addView(tabBarView);
    }

    public void setSelected(Integer index){

    }

    public void onClick(View target){
        Integer tag = (Integer)target.getTag();
        if (tag != null){
            adapter.tabButtonClicked(tag);
        }
    }
}
