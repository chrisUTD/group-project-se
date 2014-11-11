package com.example.chris.group_project;


import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Contact class for holding data loaded from the ContactProvider in the Android system.
 * Creation and population of the fields in this class should be handled by the ContactManager class.
 */
public class Contact
{
    /**
     * Unique ID assigned by the ContactManager in order that the contacts are loaded from the
     * Contact Provider.
     */
    private Integer id;
    /**
     * Android-assigned key associated with the contact. Enables lookups of contact even if
     * CONTACT_ID changes
     */
    private String LOOKUP_KEY;
    /**
     * Android-assigned ID associated with the contact. This ID can change.
     */
    private String CONTACT_ID; // Android database id

    /**
     * The primary name of the contact. First and Last if known.
     */
    private String displayName;
    /**
     * Contact's first name, if known. Not loaded initially.
     */
    private String firstName;
    /**
     * Contact's middle name, if known. Not loaded initially.
     */
    private String middleName;
    /**
     * Contact's last name, if known. Not loaded initially.
     */
    private String lastName;
    /**
     * List of phone numbers stored as strings associated with this contact. Not loaded initially
     */
    private ArrayList<String> phoneNumbers;
    /**
     * List of email addresses stored as strings and associated with this contact. Not loaded initially.
     */
    private ArrayList<String> emailAddresses;
    /**
     * URI of the contact's associated photo.
     */
    private String photoUri;
    /**
     * Android-provided property.
     */
    private int timesContacted;
    /**
     * Android-provided property.
     */
    private long lastTimeContacted;
    /**
     * Android-provided property.
     */
    private int starred;
    /**
     * Android-provided property.
     */
    private int sendToVoicemail;

    /**
     * Flag to indicate if the contact's data has been altered since initally loaded by the
     * ContactManager
     */
    private boolean containsNewData = false;
    /**
     * Flag to indicate if all contact details have been loaded (e.g. Phone Number)
     */
    private boolean containsDetails = false;

    /**
     * Constructor to create empty Contact.
     */
    public Contact(){
        phoneNumbers = new ArrayList<String>();
        emailAddresses = new ArrayList<String>();
    }

    /**
     * Call this to have Contact search its data for a given term.
     * Currently only searches displayName and other names, if they are already loaded.
     * @param term string to search for
     * @return true if contains term; else false.
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

    /**
     *
     * @return id.
     */
    public Integer getId(){return id;}

    /**
     * Set id.
     * @param id
     */
    public void setId(int id){this.id = id;}

    /**
     * Get Lookup key.
     * @return
     */
    public String getLOOKUP_KEY() {
        return LOOKUP_KEY;
    }

    /**
     * Set lookup_key.
     * @param LOOKUP_KEY
     */
    public void setLOOKUP_KEY(String LOOKUP_KEY) {
        this.LOOKUP_KEY = LOOKUP_KEY;
    }

    /**
     * Get CONTACT_ID
     * @return CONTACT_ID
     */
    public String getCONTACT_ID() {
        return CONTACT_ID;
    }

    /**
     * Set CONTACT_ID
     * @param CONTACT_ID
     */
    public void setCONTACT_ID(String CONTACT_ID) {
        this.CONTACT_ID = CONTACT_ID;
    }

    /**
     * Get first name.
     * @return first name
     */
    public String getFirstName(){return firstName;}

    /**
     * Set first name
     * @param firstName
     */
    public void setFirstName(String firstName){this.firstName = firstName;}

    /**
     * Get middle name.
     * @return middle name.
     */
    public String getMiddleName(){return middleName;}

    /**
     * Set middle name.
     * @param midddleName
     */
    public void setMiddleName(String midddleName){this.middleName = midddleName;}

    /**
     * Get last name.
     * @return last name.
     */
    public String getLastName(){return lastName;}

    /**
     * Set last name.
     * @param lastName
     */
    public void setLastName(String lastName){this.lastName = lastName;}

    /**
     * Get phone numbers
     * @return array of phone numbers represented as strings.
     */
    public ArrayList<String> getPhoneNumbers(){return phoneNumbers;}

    /**
     * Get email addresses.
     * @return array of phone numbers represented as strings.
     */
    public ArrayList<String> getEmailAddresses(){return emailAddresses;}

    /**
     * Get Display Name.
     * @return display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set display name.
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Add a phone number to the contact.
     * @param n number to add.
     */
    public void putPhoneNumber(String n) {
        this.phoneNumbers.add(n);
    }

    /**
     * Add an email address to the contact.
     * @param e email address to add.
     */
    public void putEmailAddress(String e) {
        this.emailAddresses.add(e);
    }

    /**
     * Get URI for the photo of this contact.
     * @return URI
     */
    public String getPhotoUri() {
        return photoUri;
    }

    /**
     * Set photo URI
     * @param photoUri
     */
    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    /**
     * Get number of times this person has been contacted.
     * @return int - number of times contacted
     */
    public int getTimesContacted() {
        return timesContacted;
    }

    /**
     * Set times contacted.
     * @param timesContacted
     */
    public void setTimesContacted(int timesContacted) {
        this.timesContacted = timesContacted;
    }

    /**
     * Get last time contacted.
     * @return long representing time stamp of last contact.
     */
    public long getLastTimeContacted() {
        return lastTimeContacted;
    }

    /**
     * Set last time contacted.
     * @param lastTimeContacted
     */
    public void setLastTimeContacted(long lastTimeContacted) {
        this.lastTimeContacted = lastTimeContacted;
    }

    /**
     * Get indicator that this contact is starred.
     * @return 0 or 1 to indicate starred.
     */
    public int getStarred() {
        return starred;
    }

    /**
     * Set starred
     * @param starred 0 or 1
     */
    public void setStarred(int starred) {
        this.starred = starred;
    }

    /**
     * Get whether this contact should be sent to voicemail.
     * @return 0 or 1
     */
    public int getSendToVoicemail() {
        return sendToVoicemail;
    }

    /**
     * Set send to voice mail
     * @param sendToVoicemail 0 or 1
     */
    public void setSendToVoicemail(int sendToVoicemail) {
        this.sendToVoicemail = sendToVoicemail;
    }

    /**
     * Get containsNewData
     * @return containsNewData or not
     */
    public boolean containsNewData() {
        return containsNewData;
    }

    /**
     * Set whether contact contains new data
     * @param containsNewData
     */
    public void setContainsNewData(boolean containsNewData) {
        this.containsNewData = containsNewData;
    }

    /**
     * Get whether the contact contains details.
     * @return containsDetails
     */
    public boolean containsDetails() {
        return containsDetails;
    }

    /**
     * Set whether the contact contains details.
     * @param containsDetails
     */
    public void setContainsDetails(boolean containsDetails) {
        this.containsDetails = containsDetails;
    }

    /**
     * Generates the Lookup URI for this contact, used to identify this contact for intent launching
     * and if the contact's CONTACT_ID is changed by Android.
     * @return URI for the contact.
     */
    public Uri getUri()
    {
        return ContactsContract.Contacts.getLookupUri(Long.parseLong(getCONTACT_ID()), getLOOKUP_KEY());
    }

}
