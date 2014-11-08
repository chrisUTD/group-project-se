package com.example.chris.group_project;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class GroupActivity extends Activity {

    private Long groupId;
    private ContactListAdapter adapter;
    private GroupManager groupManager;

    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent pickerIntent = new Intent(this, ContactPickerActivity.class);
            startActivityForResult(pickerIntent, PICK_CONTACT_REQUEST);

            return true;
        }
        else if (id == R.id.action_delete_group) {
            groupManager.delete(groupManager.get(groupId));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("GROUP ACTIVITY", "on activity result");
        if (requestCode == PICK_CONTACT_REQUEST){
            Log.d("GROUP ACTIVITY", "on activity result");
            if (resultCode == RESULT_OK){
                ArrayList<Integer> contactIds = data.getIntegerArrayListExtra("contacts");

                Log.d("GROUP ACTIVITY", "contactIds " + contactIds.toString());

                ArrayList<Contact> contactsToAddToGroup = new ArrayList<Contact>();
                for (int id : contactIds){
                    Log.d("GROUP ACTIVITY", " "+id);
                    Contact c = ContactManager.getInstance(this).get(id);
                    if (c != null){
                        contactsToAddToGroup.add(c);
                    }
                }
                groupManager.addContactsToGroup(contactsToAddToGroup, groupManager.get(groupId));
            }
        }
        ContactManager.getInstance(this).filterByGroup(groupId, groupManager);
    }

    @Override
    public boolean isFinishing() {
        return super.isFinishing();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_group, container, false);
            return rootView;
        }
    }
}
