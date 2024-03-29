package com.example.chris.group_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * GroupManager class loads data from the Group database and creates group objects to store the data.
 * It also handles adding and deleting contacts from a group, and adding and deleting groups.
 *
 * The manager can register listeners for notification of changes to the contacts stored in the database
 */
public class GroupManager implements ModelChangeNotifier {
    /** The groups stored in the manager **/
    private ArrayList<Group> groups;
    /** GroupManager static instance. **/
    private static GroupManager instance;
    /** Database Helper object to retreive the databse **/
    private GroupDbHelper dbHelper;
    /** Group database reference **/
    private SQLiteDatabase database;
    /** Reference to current context **/
    private Context context;

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


    private GroupManager(Context context){
        this.context = context;
        this.dbHelper = new GroupDbHelper(context);
        this.database = dbHelper.getWritableDatabase();
        this.groups = new ArrayList<Group>();

    }
    public static GroupManager getInstance(Context context)
    {
        if (instance == null){
            instance = new GroupManager(context);
            instance.load();
        }
        return instance;
    }

    public Group get(long groupId){
        Group foundGroup = null;
       // Log.d("GET GROUP num of groups: ", ""+groups.size());
        for (Group g : groups){
            //Log.d("get GROUP", "groupId input: " + groupId + "g id: " + g.getId() + " " + g.getName() );
            if (g.getId() == groupId){
                foundGroup = g;
                break;
            }
        }
        return foundGroup;
    }

    public ArrayList<Group> getGroups()
    {
        return groups;
    }

    /**
     * Load the Groups from the database. Additionally, each Group is populated with
     * CONTACT_IDs from the ContactToGroup table to identify which contacts belong to the group.
     */
    public void load(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                GroupContract.Group.TABLE_NAME, // table name
                null, // project, column to return
                null, // selection, columns for WHERE
                null, // selection, values for WHERE
                null, // don't group rows
                null, // don't filter by row getGroups
                GroupContract.Group._ID + " ASC" // order by id
        );

        while (cursor.moveToNext()){
            Group g = new Group();
            g.setId(cursor.getLong(cursor.getColumnIndex(GroupContract.Group._ID)));
            g.setName(cursor.getString(cursor.getColumnIndex(GroupContract.Group.COLUMN_NAME_TITLE)));

            g.setContainsDetails(false);
            g.setContainsNewData(false);
            groups.add(g);
        }
        notifyListeners(this);
    }

    public Group getGroupDetails(Group group){
        group.getCONTACT_IDs().clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                GroupContract.ContactToGroup.TABLE_NAME, // table name
                null, // project, column to return
                GroupContract.ContactToGroup.COLUMN_NAME_GROUP_ID + " = ? ", // selection, columns for WHERE
                new String[]{""+group.getId()}, // selection, values for WHERE
                null, // don't group rows
                null, // don't filter by row
                GroupContract.ContactToGroup.COLUMN_NAME_CONTACT_ID + " ASC" // order by id
        );

        while (cursor.moveToNext()){
            group.getCONTACT_IDs().add(""+cursor.getLong(cursor.getColumnIndex(GroupContract.ContactToGroup.COLUMN_NAME_CONTACT_ID)));

            group.setContainsDetails(true);
        }
        notifyListeners(this);
        return group;
    }

    /**
     * Clear the array of Group objects and reload from database.
     */
    public void refresh(){
        groups.clear();
        load();
    }

    /**
     * Insert a new group into the GroupManager and into the Group database table.
     * @param group the new group to insert. group.name should be !null, group.id is assumed null
     * @return the group with the new row id of the group in the database assigned to group.id
     */
    public Group insert(Group group){
        if (group.getName() != null){
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(GroupContract.Group.COLUMN_NAME_TITLE, group.getName());

            long newRowId;

            newRowId = db.insert(
                    GroupContract.Group.TABLE_NAME,
                    null,
                    values);

            if (newRowId != -1){
                group.setId(newRowId);
            }
        }
        groups.add(group);
        notifyListeners(this);
        return group;
    }

    /**
     * Delete specified group from the database and the Manager.
     * @param group group to delete
     */
    public void delete(Group group){
        if (groups.contains(group)
                && group.getId() != -1){

            groups.remove(groups.indexOf(group));

            for (String CONTACT_ID : group.getCONTACT_IDs()){
                removeContactIdForGroup(CONTACT_ID, group);
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Delete from Group table
            String groupSelection = GroupContract.Group._ID + " LIKE ?";
            String[] groupSelectionArgs = { String.valueOf(group.getId())};
            db.delete(GroupContract.Group.TABLE_NAME, groupSelection, groupSelectionArgs);

            // Delete from ContactToGroup table
            String contactToGroupSelection = GroupContract.ContactToGroup.COLUMN_NAME_GROUP_ID + " LIKE ?";
            String[] contactToGroupSelectionArgs = { String.valueOf(group.getId())};
            db.delete(GroupContract.ContactToGroup.TABLE_NAME, contactToGroupSelection, contactToGroupSelectionArgs);
        }
        notifyListeners(this);
    }

    /**
     * Add specified contacts to the group and update the database.
     * @param contactsToAdd array of contacts to add to the group
     * @param group group to add contact to.
     */
    public void addContactsToGroup(ArrayList<Contact> contactsToAdd, Group group) {
        getGroupDetails(group);
        for (Contact contact : contactsToAdd) {
            String contactIdToAdd = contact.getCONTACT_ID();
            if (contactIdToAdd != null) {
                Contact contactToAdd = ContactManager.getInstance(context).getContactByCONTACT_ID(contactIdToAdd);
                if (!contactIsInGroup(contactToAdd, group)) {
                    insertContactIdForGroup(contactIdToAdd, group); // ADD TO DATABASE
                    getGroupDetails(group); // refreshes the data
                }
            }
        }
        notifyListeners(this);
    }

    /**
     * Remove specified contacts from the group and update the database.
     * @param contactsToRemove array of contacts to remove.
     * @param group group to remove.
     */
    public void removeContactsFromGroup(ArrayList<Contact> contactsToRemove, Group group){
        getGroupDetails(group);
        for (Contact contact : contactsToRemove) {
            String contactIdToRemove = contact.getCONTACT_ID();
            if (contactIdToRemove != null) {
                Contact contactToRemove = ContactManager.getInstance(context).getContactByCONTACT_ID(contactIdToRemove);
                if (contactIsInGroup(contactToRemove, group)) {
                    removeContactIdForGroup(contactIdToRemove, group); // ADD TO DATABASE
                    getGroupDetails(group); // refreshes the data
                }
            }
        }
        notifyListeners(this);
    }

    /**
     * Determine whether the specified contact belongs to the group specified by id.
     * @param findContact contact to find.
     * @param groupId group to check.
     * @return true if in group false if not in group.
     */
    public boolean contactIsInGroup(Contact findContact, long groupId){
        for (Group g : groups){
            if (g.getId() == groupId){
                return contactIsInGroup(findContact, g);
            }
        }
        return false;
    }
    /**
     * Determine whether the specified contact belongs to the group.
     * @param findContact contact to find.
     * @param group group to check.
     * @return true if in group false if not in group.
     */
    public boolean contactIsInGroup(Contact findContact, Group group){
        getGroupDetails(group);
        for (String CONTACT_ID : group.getCONTACT_IDs()){
            if (CONTACT_ID.compareTo(findContact.getCONTACT_ID()) == 0){
                return true;
            }
        }
        return false;
    }

    /**
     * Get all groups that contain the contact.
     * @param contact
     * @return
     */
    public ArrayList<Group> getGroupsForContact(Contact contact){
        ArrayList<Group> groupsForContact = new ArrayList<Group>();
        for (Group g : groups){
            if (contactIsInGroup(contact, g)){
                groupsForContact.add(g);
            }
        }
        return groupsForContact;
    }

    /**
     * Private method to insert the given contact_id into the database.
     * @param CONTACT_ID
     * @param group
     */
    private void insertContactIdForGroup(String CONTACT_ID, Group group){
        if (group.getId() != -1
                && CONTACT_ID != null){
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.execSQL("INSERT INTO " + GroupContract.ContactToGroup.TABLE_NAME + "" +
                    "("+GroupContract.ContactToGroup.COLUMN_NAME_CONTACT_ID+", " +
                    GroupContract.ContactToGroup.COLUMN_NAME_GROUP_ID+")" +
                    " VALUES('"+ CONTACT_ID +"', " + group.getId() + " );");
        }
    }

    /**
     * Private method to remove the contact_id from the database.
     * @param CONTACT_ID
     * @param group
     */
    private void removeContactIdForGroup(String CONTACT_ID, Group group){
        if (group.getId() != -1
                && CONTACT_ID != null){
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Log.d("GROUP MANAGER", "removing form group contact: " + CONTACT_ID);

            db.execSQL("DELETE FROM " + GroupContract.ContactToGroup.TABLE_NAME + " " +
                    "WHERE " + GroupContract.ContactToGroup.COLUMN_NAME_CONTACT_ID + " LIKE " + " '" + CONTACT_ID + "' " +
                    "AND " + GroupContract.ContactToGroup.COLUMN_NAME_GROUP_ID + " = " + group.getId());
        }
    }

    /**
     * Private class to provide access to the SQLite database. It is included as a private subclass
     * here because only the Group manager makes use of it (currently).
     */
    private class GroupDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "ContactDatabase.db";

        public GroupDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db){
            db.execSQL(GroupContract.SQL_CREATE_GROUP_TABLE);
            db.execSQL(GroupContract.SQL_CREATE_CONTACT_TO_GROUP_TABLE);
            db.execSQL(GroupContract.SQL_PREPOPULATE_GROUP_TABLE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            //TODO: Does this need to be implemented?
        }
    }


}