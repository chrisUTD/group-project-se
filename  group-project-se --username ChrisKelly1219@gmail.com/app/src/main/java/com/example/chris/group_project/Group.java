package com.example.chris.group_project;


import java.util.ArrayList;

/**
 * Created by nahuecht on 10/20/2014.
 */

public class Group
{
    private int id;

    private String name;
    /**
     * Array that holds Contact objects for the purpose of storing references to Android managed
     * contacts. Those references are stored in the ContactToGroup table and when the GroupManager
     * creates the groups, it loads those references so that Contacts that have been added to each
     * group can be retrieved from the ContactManager, which separately loads its contacts from
     * the Android ContactsProvider
     */
    private ArrayList<String> CONTACT_IDs;

    public Group(){
        this.CONTACT_IDs = new ArrayList<String>();
    }

    public Group(int id, String name)
    {
        this();
        this.id = id;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ArrayList<String> getCONTACT_IDs(){
        return CONTACT_IDs;
    }
}
