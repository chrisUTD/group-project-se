package com.example.chris.group_project;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by nahuecht on 10/20/2014.
 */
public class ContactManager
{
    private ArrayList<Contact> contacts;
    private static ContactManager instance;
    private static Context context;
    private static Integer count;

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
                break;
            }
        }

        return found ? contacts.get(index) : null;
    }

    /**
     * Search for a term within each contact in the contacts array.
     * @param term
     * @return array of contacts that contained the search term.
     */
    public ArrayList<Contact> search(String term)
    {
        ArrayList<Contact> results = new ArrayList<Contact>();
        for(Contact temp : contacts)
        {
            if(temp.search(term))
            {
                results.add(temp);
            }
        }
        return results;
    }

    public void save(Contact c){} //writes new data to databoase
    public void commit(){}//perhaps is unecessary, allows you to have access to chnages locally right away, this will walk to the android database


    /**
     * Private constructor - called by getInstance when instance had not already been created.
     * Calls load() to load contacts into the ContactManager object.
     * @param context - activity context
     */
    private ContactManager(Context context){
        this.context = context;
        contacts = new ArrayList<Contact>();
        count = 0;
        load();
    }

    /**
     * Static getInstance method returns the single instantiated instance of the JRContactLoader if
     * it is already instantiated, or else creates it and returns the new instance.
     * @param context - activity context
     * @return the instance of the ContactManager
     */
    public static ContactManager getInstance(Context context){
        if (instance == null){
            instance = new ContactManager(context);
        }
        return instance;
    }

    /**
     * Load contacts from the Android ContactProvider system.
     * For each contact a new ContactManager object is created and added to the contacts array.
     *
     * Each contact is given an id, which is nothing more than the index of that item in the
     * arraylist. Since the contacts are retrieved in ascending order according to the Display Name
     * of the contact, then the ids are also assigned in that order.
     *
     * If sorting operations are performed, they should be performed on a separate array to retain
     * the id == index relationship.
     *
     * After new contacts are added to the Android System, refresh() should be called to reload all
     * data. At this point, all old id's will be useless.
     *
     * Contacts will also have CONTACT_ID and LOOKUP_KEY, which are assigned by the Android system.
     * LOOKUP_KEY in particular should refer to the same contact even after insertions.
     *
     * To save time, not all data is loaded into the contact objects:
     *      id, LOOKUP_KEY, CONTACT_ID, displayName, timesContacted, lastTimeContacted, starred
     *      sentToVoicement, and photoUri are loaded.
     *
     * To Load other attributes into a contact object, you must call ContactManager.getContactDetails(id)
     */
    private void load(){

        //Log.d("ContactManager", "load()");

        Cursor contactCursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if (contactCursor.getCount() > 0){
            while (contactCursor.moveToNext()){

                Contact contact = new Contact();
                count++;
                contact.setId(count - 1);

                //Log.d("JRContactManager", "new contact id: " + contact.getId());

                //get contact id:
                String contactId = contactCursor.getString(
                        contactCursor.getColumnIndex(ContactsContract.Contacts
                                ._ID));

                contact.setCONTACT_ID(contactId);

                // Retrieve and set Data from the ContactsContract.Contacts Columns
                contact.setLOOKUP_KEY(contactCursor
                        .getString(contactCursor
                                .getColumnIndex(ContactsContract.Contacts
                                        .LOOKUP_KEY)));
                contact.setDisplayName(contactCursor
                        .getString(contactCursor
                                .getColumnIndex(ContactsContract.Contacts
                                        .DISPLAY_NAME_PRIMARY)));

                contact.setTimesContacted(contactCursor
                        .getInt(contactCursor
                                .getColumnIndex(ContactsContract.Contacts
                                        .TIMES_CONTACTED)));
                contact.setLastTimeContacted(contactCursor
                        .getLong(contactCursor
                                .getColumnIndex(ContactsContract.Contacts
                                        .LAST_TIME_CONTACTED)));
                contact.setStarred(contactCursor
                        .getInt(contactCursor
                                .getColumnIndex(ContactsContract.Contacts
                                        .STARRED)));
                contact.setSendToVoicemail(contactCursor
                        .getInt(contactCursor
                                .getColumnIndex(ContactsContract.Contacts
                                        .SEND_TO_VOICEMAIL)));


                contact.setPhotoUri(contactCursor
                        .getString(contactCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone
                                        .PHOTO_URI)));

                //Log.d("JRContactManager", "new contact name " + contact.getDisplayName());

                // Add loaded Contact to the array
                contact.setContainsNewData(false);
                contact.setContainsDetails(false);
                contacts.add(contact);
            }
            contactCursor.close();
        }
    }

    /**
     * Clear and reload contacts from the contacts array.
     */
    public void refresh(){
        contacts.clear();
        count = 0;
        load();
    }

    /**
     * Get the contacts array.
     * @return array of loaded contacts. NOTE: not all data is guaranteed to be loaded into each
     * contact. To ensure that all data is loaded for a given contact, use
     * JRContactManager.getContactDetails(contactId) for the contact whose details you want to load
     */
    public ArrayList<Contact> contacts(){
        return contacts;
    }

    /**
     * MAY NOT NEED THIS
     */
//    public void commit(){
//        for (JRContact contact : contacts){
//            if (contact.containsNewData()){
//
//            }
//        }
//    }

    public Contact getContactDetails(Contact contactWithoutDetails){
        return getContactDetails(contactWithoutDetails.getId());
    }

    /**
     * Load all the contact details for a given contact. This method will find the given object by
     * id in the contacts array, and then load the details from the Android ContactProvider, and
     * then return a reference to that contact.
     * @param contactId the JRContactManager id of the JRContact object
     * @return the JRContact object.
     */
    public Contact getContactDetails(Integer contactId){
        Contact contact = contacts.get(contactId);
        if (!contact.containsDetails()){
            // Get First, Middle, and Last Names

            Cursor nameCursor = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?",
                    new String[]{
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                            contact.getCONTACT_ID() },
                    null);
            while(nameCursor.moveToNext()) {
                contact.setFirstName(nameCursor
                        .getString(nameCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName
                                        .GIVEN_NAME)));
                contact.setMiddleName(nameCursor
                        .getString(nameCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName
                                        .MIDDLE_NAME)));
                contact.setLastName(nameCursor
                        .getString(nameCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName
                                        .FAMILY_NAME)));
            }
            nameCursor.close();
            // Get and Put Phone Numbers

            Cursor phoneCursor = context.getContentResolver()
                    .query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                            new String[]{contact.getCONTACT_ID()},
                            null);
            while (phoneCursor.moveToNext()){
                contact.putPhoneNumber(phoneCursor
                        .getString(phoneCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone
                                        .NUMBER)));
            }
            phoneCursor.close();

            // Get and Put Email Addresses

            Cursor emailCursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + "= ?",
                    new String[]{contact.getCONTACT_ID()},
                    null);
            while (emailCursor.moveToNext()){
                contact.putEmailAddress(emailCursor
                        .getString(emailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds
                                        .Email.DATA)));
            }
            emailCursor.close();
            contact.setContainsDetails(true);
        }

        // Get and put Groups
        //TODO: LOAD GROUPS INTO THE groups ARRAY

        return contact;
    }

    /**
     * Find contact that has the provided CONTACT_ID, which is the Android id for that aggregate contact.
     * @param findCONTACT_ID id of the contact you are trying to find.
     * @return contact
     */
    public Contact getContactByCONTACT_ID(String findCONTACT_ID){
        Contact foundContact = null;
        for (Contact contact : contacts){
            if (contact.getCONTACT_ID() != null
                    && contact.getCONTACT_ID() == findCONTACT_ID){
                foundContact = contact;
                break;
            }
        }
        return foundContact;
    }

    public void deleteContact(Contact contact)
    {
        ContentResolver cr = context.getContentResolver();
        cr.delete(contact.getUri(),null,null);
        refresh();
    }

}
