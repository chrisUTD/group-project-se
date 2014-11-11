package com.example.chris.group_project;


import java.util.ArrayList;

/**
 * Model object that stores information for a single Group.
 */

public class Group
{
    /**
     * Database id of group. -1 == "null", does not exist in database.
     */
    private long id = -1;
    /**
     * Name of the group.
     */
    private String name;
    /**
     * Array that holds Contact objects for the purpose of storing references to Android managed
     * contacts. Those references are stored in the ContactToGroup table and when the GroupManager
     * creates the groups, it loads those references so that Contacts that have been added to each
     * group can be retrieved from the ContactManager, which separately loads its contacts from
     * the Android ContactsProvider
     */
    private ArrayList<String> CONTACT_IDs;
    /**
     * Indicates that the group object has all details loaded.
     */
    private boolean containsDetails = false;
    /**
     * Indicates that the group object has been modified after being loaded.
     */
    private boolean containsNewData = false;

    /**
     * Construct empty group object.
     */
    public Group(){
        this.CONTACT_IDs = new ArrayList<String>();
    }

    /**
     * Construct group with given name.
     * @param name
     */
    public Group(String name){
        this();
        this.name = name;
    }

    /**
     * Construct group with given id and name.
     * @param id
     * @param name
     */
    public Group(int id, String name)
    {
        this(name);
        this.id = id;
    }

    /**
     * Get the database id of the group
     * @return id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Set the database id of the group.
     * @param id
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Get the name of the group.
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the group
     * @param name name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Determine whether all group details are loaded
     * @return
     */
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
