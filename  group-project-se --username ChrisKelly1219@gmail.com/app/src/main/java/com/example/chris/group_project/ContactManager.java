package com.example.chris.group_project;


import java.util.ArrayList;

/**
 * Created by nahuecht on 10/20/2014.
 */
public class ContactManager
{
    private ArrayList<Contact> contacts;
    private ContactManager instance;

    public ContactManager getInstance(){return instance;}


    /* I think this is the right idea here but im not really sure.
     * Also this is an n^2 search because arraylist is already slow and
     * im doing another linear search, perhaps use a min heap instead?
     */
    public Contact get(int id)
    {
        boolean found = false;
        int index = -1;
        for(int i = 0; i < contacts.size(); i++)
        {
            if(contacts.get(i).getId() == id)
            {
                found = true;
                index = i;
            }
        }
        return found ? contacts.get(index) : null;
    }

    /*May want to change return type here to array list because the search may
    have more than 1 result. We may be able to merge this with the list by group.
     */
    public Contact search(String term)
    {

        for(Contact temp : contacts)
        {
            if(temp.search(term))
            {
                return temp;
            }
        }
        return null;
    }

    public void save(Contact c){} //writes new data to databoase
    public void commit(){}//perhaps is unecessary, allows you to have access to chnages locally right away, this will walk to the android database

}
