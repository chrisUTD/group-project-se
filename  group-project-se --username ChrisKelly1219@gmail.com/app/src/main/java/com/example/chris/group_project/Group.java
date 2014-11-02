package com.example.chris.group_project;


import java.util.ArrayList;

/**
 * Created by nahuecht on 10/20/2014.
 */

public class Group
{
    private long id = -1; // "-1" stands in for "null", indicates a group that does not exist in DB
    private String name;
    /**
     * Array that holds Contact objects for the purpose of storing references to Android managed
     * contacts. Those references are stored in the ContactToGroup table and when the GroupManager
     * creates the groups, it loads those references so that Contacts that have been added to each
     * group can be retrieved from the ContactManager, which separately loads its contacts from
     * the Android ContactsProvider
     */
    private ArrayList<String> CONTACT_IDs;
    private boolean containsDetails = false;
    private boolean containsNewData = false;

    public Group(){
        this.CONTACT_IDs = new ArrayList<String>();
    }

    public Group(String name){
        this();
        this.name = name;
    }

    public Group(int id, String name)
    {
        this(name);
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
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

    public boolean containsDetails() {
        return containsDetails;
    }

    public void setContainsDetails(boolean containsDetails) {
        this.containsDetails = containsDetails;
    }

    public boolean containsNewData() {
        return containsNewData;
    }

    public void setContainsNewData(boolean containsNewData) {
        this.containsNewData = containsNewData;
    }

    public ArrayList<String> getCONTACT_IDs(){
        return CONTACT_IDs;
    }

}
