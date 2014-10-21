package com.example.chris.group_project;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Jonny on 10/20/14.
 */
public class TabBar extends LinearLayout {

    public TabBar(Context context){
        super(context);
    }

    // To allow instantiation in xml
    public TabBar(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public TabBar(Context context, ViewGroup parent, TabBarAdapter delegate){
        super(context);
        ViewGroup tabBarView = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.tab_bar, parent, false);

        parent.addView(tabBarView);
    }

    public void setSelected(Integer index){

    }
}
