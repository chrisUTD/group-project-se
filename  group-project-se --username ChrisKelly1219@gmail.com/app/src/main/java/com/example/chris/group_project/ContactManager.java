package com.example.chris.group_project;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Class that loads information from Android ContactProvider and creates Contact objects to hold the
 * data. The class provides methods for laoding, rereshing, filtering and unfiltering the list for
 * searching and by group. Filtering by group depends upon GroupManager object.
 *
 * This class provides access to a singleton object in order to preserve the lifetime of the object
 * across activity creation and destruction.
 *
 * The manager can register listeners for notification of changes to the contacts stored in the database
 */
public class ContactManager implements ModelChangeNotifier {
    /**
     * ArrayList that hold references to all contacts loaded from the Android ContactProvider.
     * Public access is not provided to this list.
     */
    private ArrayList<Contact> contacts;
    /**
     * This ArrayList holds all or a subset of the contacts stored in the ContactManager. This list
     * is filtered based on search terms or by group.
     * Public access is provided to this list only.
     */
    private ArrayList<Contact> filteredContacts;
    /**
     * The static instance of the class that holds data.
     */
    private static ContactManager instance;
    /**
     * Reference to the current context.
     */
    private static Context context;
    /**
     * Number of items stored in the manager.
     */
    private static Integer count;

    /*************************** MODEL CHANGE NOTIFIER IMPLEMENTATION *****************************/
    private static ArrayList<ModelChangeListener> listeners = new ArrayList<ModelChangeListener>();

    @Override
    public void notifyListeners(ModelChangeNotifier model){
        if (listeners != null && listeners.size() != 0) {
            for (ModelChangeListener l : listeners) {
                if (l != null) { // Needed?
                    l.onModelChange(model);
                }
            }
        }
    }
    @Override
    public void registerListener(ModelChangeListener listener){
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    @Override
    public void unregisterListener(ModelChangeListener listener){
        if (listeners != null){
            listeners.remove(listener);
        }
    }
    @Override
    public void touch(){
        notifyListeners(this);
    }
    /**********************************************************************************************/

    /**
     * Retrieve a contact by identifying the id given by the ContactManager upon initial creation.
     * @param id contact's id
     * @return the contact
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

        /*Sorting the ArrayList by contact display name as that is what is searched
        first. That makes the search O(N lg N) as the sort takes the most time and the lookup
        is linear after that. Contact.search is a constant time compare to check of the 3 different
        names associated with contact so sorting them means that the lower alphabetic
        contacts will be hit rather quickly and the later contacts will be hit in O(n lg n)
        at the worst which is much better than n^3.
         */
        Collections.sort(results, new Comparator<Contact>()
        {
        @Override
                public int compare(Contact c1, Contact c2)
                {
                    return c1.getDisplayName().compareToIgnoreCase(c2.getDisplayName());
                }
        });

        for(Contact temp : contacts)
        {
            getContactDetails(temp);
            if(temp.search(term))
            {
                results.add(temp);
            }
        }
        return results;
    }

    /**
     * Private constructor - called by getInstance when instance had not already been created.
     * Calls load() to load contacts into the ContactManager object.
     * @param context - activity context
     */
    private ContactManager(Context context){
        this.context = context;
        contacts = new ArrayList<Contact>();
        filteredContacts = new ArrayList<Contact>();
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
        Cursor contactCursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC");
        if (contactCursor.getCount() > 0){
            while (contactCursor.moveToNext()){

                Contact contact = new Contact();
                count++;
                contact.setId(count - 1);

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

                // Add loaded Contact to the array
                contact.setContainsNewData(false);
                contact.setContainsDetails(false);
                contacts.add(contact);
            }
            filteredContacts = new ArrayList<Contact>(contacts);
            contactCursor.close();
            notifyListeners(this);
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
        return filteredContacts;
    }

    /**
     * Load the additional details for the specified contact.
     * @param contactWithoutDetails
     * @return contact with details loaded.
     */
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
                    && Long.parseLong(contact.getCONTACT_ID()) == Long.parseLong(findCONTACT_ID)){
                foundContact = contact;
                break;
            }
        }
        return foundContact;
    }

    /**
     * Filter the publicly accessible array of contacts by search term.
     * @param term basis of filtering
     * @return filtered contacts.
     */
    public ArrayList<Contact> filterBySearchTerm(String term){
        filteredContacts.clear();
        for (Contact c : contacts){
           if (c.search(term)){
               filteredContacts.add(c);
           }
        }
        notifyListeners(this);
        return filteredContacts;
    }

    /**
     * Filter the publicly accessible array of contacts by group.
     * @param groupId GroupManager id of the group's contacts to show.
     * @param groupManager GroupManager instance that provides access to groups data.
     * @return fitlered list.
     */
    public ArrayList<Contact> filterByGroup(long groupId, GroupManager groupManager){
        filteredContacts.clear();

        Group group = groupManager.get(groupId);
        groupManager.getGroupDetails(group);

        for (String contactId : group.getCONTACT_IDs()){
            Contact contact = getContactByCONTACT_ID(contactId);
            if (contact != null){
                filteredContacts.add(contact);
            }
        }

        notifyListeners(this);
        return filteredContacts;
    }

    /**
     * Unfilters the publicly accessible array of contacts.
     * @return (un)filtered array
     */
    public ArrayList<Contact> unfilter(){
        filteredContacts = contacts;
        notifyListeners(this);
        return filteredContacts;
    }

    /**
     * Delete contact from the ContactProvider.
     * After deletion the ContactManager is refreshed from the android ContactProvider.
     * @param contact
     */
    public void deleteContact(Contact contact)
    {
        ContentResolver cr = context.getContentResolver();
        cr.delete(contact.getUri(),null,null);
        refresh();
    }

    // TODO: UPDATE THESE METHODS TO SAVE UPDATED INFO TO THE ANDROID CONTACTPROVIDER
    /**
     * Setting a contact to be automatically sent to voicemail
     * @param contact The contact which will have its send to voicemail property changed
     */
    public void blockContact(Contact contact)
    {
        contact.setSendToVoicemail(1);

        ContentValues mUpdateValues = new ContentValues();
        mUpdateValues.put(ContactsContract.Contacts.SEND_TO_VOICEMAIL, 1);

        context.getContentResolver().update(contact.getUri(), mUpdateValues, null, null);

    }

    /**
     * Setting a contact to no longer be automatically sent to voicemail
     * @param contact The contact which will have its send to voicemail property changed
     */
    public void unblockContact(Contact contact)
    {
        contact.setSendToVoicemail(0);

        ContentValues mUpdateValues = new ContentValues();
        mUpdateValues.put(ContactsContract.Contacts.SEND_TO_VOICEMAIL, 0);

        context.getContentResolver().update(contact.getUri(), mUpdateValues, null, null);
    }
}
