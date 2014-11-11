package com.example.chris.group_project;

import android.provider.BaseColumns;

/**
 * Class to document the structure of the database tables related to the Group class
 * as well as provide common commands to create and delete those tables.
 */
public final class GroupContract {

    /**
     * Empty constructor. This class should not ever be instantiated.
     */
    public GroupContract(){}

    /**
     * Inner class to define the Group table.
     */
    public static abstract class Group implements BaseColumns {
        public static final String TABLE_NAME = "contacts_group";
        public static final String COLUMN_NAME_TITLE = "title";

        //Some Default values:
        public static final String VALUE_FAVORITES = "Favorites";
        public static final String VALUE_FAMILY = "Family";
        public static final String VALUE_FRIENDS = "Friends";
        public static final String VALUE_WORK = "Work";
    }

    /**
     * Inner class to define the ContactToGroup table which stores references to android contacts
     * (identified by contact_id and lookup_key). It is here treated as if it were a table bridging
     * a many-to-many relationship with a Contact table, but there is no Contact table since contacts
     * are stored using the Android ContactsProvider.
     */
    public static abstract class ContactToGroup implements BaseColumns {
        public static final String TABLE_NAME = "contacts_to_group";
        public static final String COLUMN_NAME_GROUP_ID = "group_id";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
    }

    /**
     * SQL Command to create the Group table.
     */
    public static final String SQL_CREATE_GROUP_TABLE =
            "CREATE TABLE " + Group.TABLE_NAME + " (" +
                    Group._ID + " INTEGER PRIMARY KEY, " +
                    Group.COLUMN_NAME_TITLE + " TEXT" +
                    " )";
    /**
     * SQL Command to insert the initial 'built-in' Groups that always be available to users.
     */
    public static final String SQL_PREPOPULATE_GROUP_TABLE =
            "INSERT INTO " + Group.TABLE_NAME + "(" + Group.COLUMN_NAME_TITLE + ")" +
                    "VALUES ('"+ Group.VALUE_FAVORITES +"'), ('" +
                        Group.VALUE_FAMILY + "'), ('" +
                        Group.VALUE_FRIENDS + "'), ('"+
                        Group.VALUE_WORK + "')";

    /**
     * SQL Command to delete Group table.
     */
    public static final String SQL_DELETE_GROUP_TABLE =
            "DROP TABLE IF EXISTS " + Group.TABLE_NAME;

    /**
     * SQL Command to create ContactToGroup table
     */
    public static final String SQL_CREATE_CONTACT_TO_GROUP_TABLE =
            "CREATE TABLE " + ContactToGroup.TABLE_NAME + " (" +
                    ContactToGroup._ID + " INTEGER PRIMARY KEY," +
                    ContactToGroup.COLUMN_NAME_CONTACT_ID + " TEXT," +
                    ContactToGroup.COLUMN_NAME_GROUP_ID + " INTEGER" +
                    " )";
    /**
     * SQL Command to Delete ContactToGroup Table.
     */
    private static final String SQL_DELETE_CONTACT_TO_GROUP_TABLE =
            "DROP TABLE IF EXISTS " + ContactToGroup.TABLE_NAME;
}
