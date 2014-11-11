package com.example.chris.group_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Activity that provides ability to select one or more contacts.
 */
public class ContactPickerActivity extends Activity {
    /**
     * Reference to ContactManager instance that provides the model objects to display.
     */
    private ContactManager contactManager;
    /**
     * Adapter that provides the views for the list view.
     */
    private ContactListAdapter adapter;

    /** Create activity and set up list view and create buttons with listeners **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_picker);

        contactManager = ContactManager.getInstance(this);

        long groupId = getIntent().getLongExtra("groupId", -1);
        if (groupId > 0){
            contactManager.filterByGroup(groupId, GroupManager.getInstance(this));
        }
        else {
            contactManager.refresh();
        }
        String title = getIntent().getStringExtra("title");
        if (title != null){
            setTitle(title);
        }
        String done_button_text = getIntent().getStringExtra("done_button_text");
        if (done_button_text != null){
            ((Button)findViewById(R.id.button_done)).setText(done_button_text);
        }

        adapter = new ContactListAdapter(this,
                R.layout.contact_list_item_view,
                contactManager,
                ContactListAdapter.ContactListMode.SELECT_ON_CLICK);


        ((ListView)findViewById(R.id.contact_picker_list_view)).setAdapter(adapter);


        ((Button)findViewById(R.id.button_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneButtonClicked();
            }
        });

        ((Button)findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonClicked();
            }
        });

    }

    /** Inflate the menu **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_picker, menu);
        return true;
    }
    /** Menu selection event **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Respond to Done Button being clicked.
     * Activity finishes and passes the array of selected items back through intent.
     */
    public void doneButtonClicked(){
        ArrayList<Integer> selectedItemIndices = adapter.getSelectedItems();

        Log.d("CONTACT PICKER", "selectedItemIndices " + selectedItemIndices.toString());

        Intent returnIntent = new Intent();
        ArrayList<Integer> results = new ArrayList<Integer>(adapter.getSelectedItems());
        returnIntent.putIntegerArrayListExtra("contacts", results);
        setResult(RESULT_OK, returnIntent);
        adapter.unselectAllItems();

        finish();
    }

    /**
     * Respond to Cancel button being clicked.
     * Activity finishes without returning any results.
     */
    public void cancelButtonClicked(){
        setResult(RESULT_CANCELED);
        adapter.unselectAllItems();

        finish();
    }
}
