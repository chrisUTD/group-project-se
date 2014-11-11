package com.example.chris.group_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Activity that shows the contacts that are identified with a particular group.
 */

public class GroupActivity extends Activity {
    /**
     * ID of the group that the activity will display.
     */
    private Long groupId;
    /**
     * Reference to the adapter for the contact list view.
     */
    private ContactListAdapter adapter;
    /**
     * Reference for the GroupManager object.
     */
    private GroupManager groupManager;

    /**
     * Integer to attach to contact picker activity on launch, and identify that on return
     * the GroupActivity should add the picked contacts to the group.
     */
    static final int PICK_CONTACT_REQUEST = 1;
    /** Integer to attach to contact picker activity on launch, and identify that on return
     * the GroupActivity should remove the picked contacts from the group.
     */
    static final int PICK_CONTACT_FOR_REMOVAL_REQUEST = 2;

    /** Create the activity, set up list view to display group contacts **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupId = getIntent().getExtras().getLong("groupId");
        if (groupId != null){
            groupManager = GroupManager.getInstance(this);

            setTitle(groupManager.get(groupId).getName());

            ContactManager.getInstance(this).filterByGroup(groupId, groupManager);
            adapter = new ContactListAdapter(this,
                    R.layout.contact_list_item_view,
                    ContactManager.getInstance(this),
                    ContactListAdapter.ContactListMode.SHOW_DETAILS_ON_CLICK);
            ListView listView = (ListView)findViewById(R.id.contacts_list_view);
            listView.setAdapter(adapter);
        }

    }

    /** On resume re-filter the contacts in the ContactManager so that the list view shows only this
     * groups contacts.
     */
    @Override
    protected void onResume() {
        super.onResume();
        ContactManager.getInstance(this).filterByGroup(groupId, groupManager);
    }

    /**
     * Inflate the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group, menu);
        return true;
    }

    /**
     * Respond to menu clicks: add contacts, delete contacts, delete group.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent pickerIntent = new Intent(this, ContactPickerActivity.class);
            pickerIntent.putExtra("title", "Add Contacts...");
            pickerIntent.putExtra("done_button_text", "Add");
            startActivityForResult(pickerIntent, PICK_CONTACT_REQUEST);
            return true;
        }
        else if (id == R.id.action_delete_group) {
            groupManager.delete(groupManager.get(groupId));
            finish();
        }
        else if (id == R.id.action_remove_contact){
            Intent pickerIntent = new Intent(this, ContactPickerActivity.class);
            pickerIntent.putExtra("groupId", groupId);
            pickerIntent.putExtra("title", "Remove Contacts...");
            pickerIntent.putExtra("done_button_text", "Remove");
            startActivityForResult(pickerIntent, PICK_CONTACT_FOR_REMOVAL_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ensure that when exiting the group acitivyt the ContactManager is unfiltered so that other
     * activities can display all contacts if needed.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            // set the adapter to null so that when unfilter() is called on ContactManger the list
            // does not automatically populate with all contacts just as the activity is quiting
            ((ListView)findViewById(R.id.contacts_list_view)).setAdapter(null);
            ContactManager.getInstance(this).unfilter();
        }
    }

    /**
     * When returning from another activity that was launched for result, determine whether to
     * delete the returned list of contacts from the group or add them to the group.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Add selected contacts.
        if (requestCode == PICK_CONTACT_REQUEST){
            if (resultCode == RESULT_OK){
                ArrayList<Integer> contactIds = data.getIntegerArrayListExtra("contacts");

                ArrayList<Contact> contactsToAddToGroup = new ArrayList<Contact>();
                for (int id : contactIds){
                    Contact c = ContactManager.getInstance(this).get(id);
                    if (c != null){
                        contactsToAddToGroup.add(c);
                    }
                }
                groupManager.addContactsToGroup(contactsToAddToGroup, groupManager.get(groupId));
            }
        }
        // Delete selected contacts.
        else if(requestCode == PICK_CONTACT_FOR_REMOVAL_REQUEST){
            if (resultCode == RESULT_OK){
                ArrayList<Integer> indices = data.getIntegerArrayListExtra("contacts");
                ArrayList<Contact> contactsToRemoveFromGroup = new ArrayList<Contact>();
                for (Integer i : indices){
                    ContactManager contactManger = ContactManager.getInstance(this);
                    ArrayList<String> CONTACT_IDs = groupManager.get(groupId).getCONTACT_IDs();

                    Contact c = contactManger.getContactByCONTACT_ID(CONTACT_IDs.get(i));
                    contactsToRemoveFromGroup.add(c);
                }
                groupManager.removeContactsFromGroup(contactsToRemoveFromGroup, groupManager.get(groupId));
            }
        }
        ContactManager.getInstance(this).filterByGroup(groupId, groupManager);
    }
}
