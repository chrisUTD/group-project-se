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


public class ContactPickerActivity extends Activity {

    private ContactManager contactManager;
    private ContactListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("CONTACT PICKER ACTIVITY", "on create");

        setContentView(R.layout.activity_contact_picker);

        contactManager = ContactManager.getInstance(this);

        long groupId = getIntent().getLongExtra("groupId", -1);
        if (groupId > 0){
            contactManager.filterByGroup(groupId, GroupManager.getInstance(this));
        }
        else {
            contactManager.refresh();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_picker, menu);
        return true;
    }

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

    public void cancelButtonClicked(){
        setResult(RESULT_CANCELED);
        adapter.unselectAllItems();

        finish();
    }
}
