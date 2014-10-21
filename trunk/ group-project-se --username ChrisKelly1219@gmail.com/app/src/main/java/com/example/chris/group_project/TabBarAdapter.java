package com.example.chris.group_project;

/**
 * Created by Jonny on 10/20/14.
 */
public interface TabBarAdapter {

    public int getCount();
    public String getTabTitle(Integer index);
    public int getTabImageResourceId(Integer index);

    public void tabSelected(Integer index);

}
