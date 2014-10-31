package com.example.chris.group_project;

import java.util.ArrayList;

/**
 * Created by nahuecht on 10/20/2014.
 */
public class GroupManager
{
    private ArrayList<Group> groups;
    private GroupManager instance;

    public GroupManager getInstance()
    {
        return instance;
    }

    private ArrayList<Group> groups()
    {
        return groups;
    }

    public void load(){

    }

    public void refresh(){

    }

}