package com.example.chris.group_project;


import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by nahuecht on 10/20/2014.
 */
public class Contact
{
    //temp change
    private Integer id; // Contact Manager id
    private String LOOKUP_KEY; // Android key
    private String CONTACT_ID; // Android database id

    private String displayName;
    private String firstName;
    private String middleName;
    private String lastName;
    private ArrayList<String> phoneNumbers;
    private ArrayList<String> emailAddresses;
    private ArrayList<Integer> groups; //TODO: Determine how Groups need to be stored.
    private String photoUri;

    // Additional data about a contact stored by Android that may be useful
    private int timesContacted;
    private long lastTimeContacted;
    private int starred;
    private int sendToVoicemail;

    // Flags
    private boolean containsNewData = false;
    private boolean containsDetails = false;

    public Contact(){
        phoneNumbers = new ArrayList<String>();
        emailAddresses = new ArrayList<String>();
        groups = new ArrayList<Integer>();
    }

    /*The search in phone numbers could be trickey to do with the use of
    parenthesis in phone numbers usually. may need a different storage method
    perhaps some regex trickerey could work well here.
     */
    public boolean search(String term)
    {
        if (displayName != null && displayName.toUpperCase().contains(term.toUpperCase()))
        {
            return true;
        }
        else if (firstName != null && firstName.toUpperCase().contains(term.toUpperCase()))
        {
             return true;
        }
        else if(middleName != null && middleName.toUpperCase().contains(term.toUpperCase()))
        {
             return true;
        }
        else if(lastName != null && lastName.toUpperCase().contains(term.toUpperCase()))
        {
            return true;
        }

        return false;
    }
    public Integer getId(){return id;}
    public void setId(int id){this.id = id;}

    public String getLOOKUP_KEY() {
        return LOOKUP_KEY;
    }

    public void setLOOKUP_KEY(String LOOKUP_KEY) {
        this.LOOKUP_KEY = LOOKUP_KEY;
    }

    public String getCONTACT_ID() {
        return CONTACT_ID;
    }

    public void setCONTACT_ID(String CONTACT_ID) {
        this.CONTACT_ID = CONTACT_ID;
    }

    public String getFirstName(){return firstName;}
    public void setFirstName(String firstName){this.firstName = firstName;}

    public String getMiddleName(){return middleName;}
    public void setMiddleName(String midddleName){this.middleName = midddleName;}

    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName = lastName;}

    public ArrayList<String> getPhoneNumbers(){return phoneNumbers;}
    public void setPhoneNumbers(ArrayList<String> phoneNumbers){this.phoneNumbers = phoneNumbers;}

    public ArrayList<String> getEmailAddresses(){return emailAddresses;}
    public void setEmailAddresses(ArrayList<String> emailAddresses){this.emailAddresses = emailAddresses;}

    public ArrayList<Integer> getGroups(){return groups;}
    public void setGroups(ArrayList<Integer> groups){this.groups = groups;}


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public void putPhoneNumber(String n) {
        this.phoneNumbers.add(n);
    }

    public void putEmailAddress(String e) {
        this.emailAddresses.add(e);
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public int getTimesContacted() {
        return timesContacted;
    }

    public void setTimesContacted(int timesContacted) {
        this.timesContacted = timesContacted;
    }

    public long getLastTimeContacted() {
        return lastTimeContacted;
    }

    public void setLastTimeContacted(long lastTimeContacted) {
        this.lastTimeContacted = lastTimeContacted;
    }

    public int getStarred() {
        return starred;
    }

    public void setStarred(int starred) {
        this.starred = starred;
    }

    public int getSendToVoicemail() {
        return sendToVoicemail;
    }

    public void setSendToVoicemail(int sendToVoicemail) {
        this.sendToVoicemail = sendToVoicemail;
    }

    public boolean containsNewData() {
        return containsNewData;
    }

    public void setContainsNewData(boolean containsNewData) {
        this.containsNewData = containsNewData;
    }
    
    public boolean containsDetails() {
        return containsDetails;
    }

    public void setContainsDetails(boolean containsDetails) {
        this.containsDetails = containsDetails;
    }

    public Uri getUri()
    {
        return ContactsContract.Contacts.getLookupUri(Long.parseLong(getCONTACT_ID()), getLOOKUP_KEY());
    }

}
