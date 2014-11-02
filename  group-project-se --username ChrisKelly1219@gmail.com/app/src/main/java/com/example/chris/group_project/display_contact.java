package com.example.chris.group_project;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chris on 10/23/2014.
 */

    public class display_contact  extends Activity
    {
        ListView contactListView;
        Contact contact;
        ImageView picture;
        TextView name;

        /**
         * Empty constructor, used when launching activity via intent.
         */
        public display_contact(){}

        public display_contact( Contact temp)
        {
            contact = temp;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            // Retrieve the contact id passed through the intent when this activity is launched
            // the retrieve contact and set it as this.contact
            Intent intent = getIntent();
            if (intent != null){
                Bundle extras = intent.getExtras();
                if (extras != null){
                    Integer contactId = extras.getInt("contactId");
                    if (contactId != null){
                        Contact findContact = ContactManager.getInstance(this).get(contactId);
                        if (findContact != null){
                            if (!findContact.containsDetails()){
                                findContact = ContactManager.getInstance(this).getContactDetails(findContact);
                            }
                            contact = findContact;
                        }
                    }
                }
            }

            setContentView(R.layout.show_contact);

            name = (TextView) findViewById(R.id.sc_Name);
            picture = (ImageView) findViewById(R.id.sc_Image);

            name.setText(contact.getDisplayName());
            if(contact.getPhotoUri() != null) {
                picture.setImageURI(Uri.parse(contact.getPhotoUri()));
            }


           // populateList(); //TODO: fix null pointer exception on this method
        }
        //TODO: Fix this: contactListView has not yet been found, so this method throws null exception
        private void populateList()
        {
            ArrayAdapter<String> adapter = new phoneNumberListAdapter();
            contactListView.setAdapter((adapter));
        }


        private class phoneNumberListAdapter extends ArrayAdapter<String>
        {

            public phoneNumberListAdapter()
            {
                super (display_contact.this, R.layout.phone_number_list_view, contact.getPhoneNumbers());
            }

            @Override
            public View getView(int position, View view, ViewGroup parent)
            {

                if (view == null) {
                    view = getLayoutInflater().inflate(R.layout.phone_number_list_view, parent, false);
                }
                TextView number = (TextView) view.findViewById(R.id.listContactNumber);
                Button callButton = (Button) view.findViewById(R.id.listCallContactButton);
                Button textButton = (Button) view.findViewById(R.id.listTextContactButton);
                ArrayList<String> tempNum = contact.getPhoneNumbers();
                final String temp = tempNum.get(position);
                number.setText(temp);

                callButton.setOnClickListener( new View.OnClickListener()
                {
                    public void onClick(View temp)
                    {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+temp));
                        startActivity(Intent.createChooser(callIntent, "Call"));

                        startActivity(callIntent);
                    }
                });

                textButton.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View temp)
                    {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", "12125551212");
                        smsIntent.putExtra("sms_body","");
                        startActivity(smsIntent);
                   }

                });



                return view;
            }

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.my, menu);
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


        //TODO: ADD THIS CODE TO SOME CLICK LISTENER FOR LAUNCHING EDIT SCREEN
        // assumes that you have an object named 'contact' for which you want to launch the edit screen.
//            Uri lookupUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(contact.getCONTACT_ID()), contact.getLOOKUP_KEY());
//
//            Intent editIntent = new Intent(Intent.ACTION_EDIT);
//            editIntent.setDataAndType(lookupUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
//            editIntent.putExtra("finishActivityOnSaveCompleted", true);
//
//            ((Activity)getContext()).startActivityForResult(editIntent, 0);

    }


