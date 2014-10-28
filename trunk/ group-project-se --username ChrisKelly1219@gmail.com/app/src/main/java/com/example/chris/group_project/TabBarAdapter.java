package com.example.chris.group_project;

/**
 * Created by Jonny on 10/20/14.
 */
public interface TabBarAdapter {

    public int getCount();
    public String getTabTitle(Integer index);
    public TabBarButton getTabBarButtonForIndex(Integer index);
    public void getSelectedTab(Integer index);

    public void tabButtonClicked(Integer index);
}
