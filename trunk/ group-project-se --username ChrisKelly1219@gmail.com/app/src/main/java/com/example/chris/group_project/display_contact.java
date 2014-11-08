package com.example.chris.group_project;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chris on 10/23/2014.
 */

    public class display_contact  extends Activity
    {
        ListView phoneListView;
        ListView emailListView;
        Contact contact;
        ImageView picture;
        TextView name;
        ArrayAdapter<String> phoneNumberAdapter;
        ArrayAdapter<String> emailAdapter;
        private static int cNum;


        /**
         * Empty constructor, used when launching activity via intent.
         */
        public display_contact(){}



        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContact();
            setContentView(R.layout.show_contact);
            populateItems();

        }

        private void setContact()
        {


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
        }

        private void populateItems()
        {
            createNameView();
            createImageView();
            createEditButton();
            CreatePhoneListView();
            CreateEmailListView();
            populateContactGroupView();
        }

        private void createNameView()
        {
            name = (TextView) findViewById(R.id.sc_Name);
            name.setText(contact.getDisplayName());

        }

        private void createImageView()
        {
            picture = (ImageView) findViewById(R.id.sc_Image);

            if(contact.getPhotoUri() != null) {
                picture.setImageURI(Uri.parse(contact.getPhotoUri()));
            }
        }

        private void createEditButton()
        {
            cNum = contact.getId();
            Button edit = (Button) findViewById(R.id.edit_contact_button);

            edit.setOnClickListener( new View.OnClickListener()
            {
                public void onClick(View temp)
                {
                    Uri lookupUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(contact.getCONTACT_ID()), contact.getLOOKUP_KEY());

                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    editIntent.setDataAndType(lookupUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                    editIntent.putExtra("finishActivityOnSaveCompleted", true);
                    startActivityForResult(editIntent,0);
                }
            });

        }

        private void CreatePhoneListView()
        {
            phoneListView = (ListView) findViewById(R.id.phone_number_listView);
            phoneNumberAdapter = new phoneNumberListAdapter();
            phoneListView.setAdapter((phoneNumberAdapter));
        }

        private void CreateEmailListView()
        {
            emailListView = (ListView) findViewById(R.id.email_list_view);
            emailAdapter = new emailListAdapter();
            emailListView.setAdapter((emailAdapter));
        }

       private void populateContactGroupView()
       {
           LinearLayout temp = (LinearLayout) findViewById(R.id.display_groups);
           ArrayList<Button> groups = createGroupBtns();
           addGroupButtonsToView(temp,groups);
       }

        private ArrayList<Button> createGroupBtns()
        {
            ArrayList<Button> groupBs = new ArrayList<Button>();
            GroupManager manager = GroupManager.getInstance(this);
            ArrayList<Group> groupHolder = manager.getGroupsForContact(contact);

            for(int i=0;i<groupHolder.size();i++)
            {
                Button btn = new Button(this);
                btn.setText(groupHolder.get(i).getName());
                groupBs.add(btn);
            }

            return groupBs;
        }

        private void addGroupButtonsToView(LinearLayout temp,ArrayList<Button> groups)
        {
            for(int i=0;i<groups.size();i++)
            {
                temp.addView(
                        groups.get(i),
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                );
            }
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
                final String phoneNumber = tempNum.get(position);
                number.setText(phoneNumber);

                callButton.setOnClickListener( new View.OnClickListener()
                {
                    public void onClick(View temp)
                    {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+phoneNumber));
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
                        smsIntent.putExtra("address",phoneNumber);
                        smsIntent.putExtra("sms_body","");
                        startActivity(smsIntent);
                   }
                });



                return view;
            }

        }

        private class emailListAdapter extends ArrayAdapter<String>
        {
            public emailListAdapter()
            {
                super (display_contact.this, R.layout.email_list_view, contact.getEmailAddresses());
            }

            @Override
            public View getView(int position, View view, ViewGroup parent)
            {
                if (view == null) {
                    view = getLayoutInflater().inflate(R.layout.email_list_view, parent, false);
                }
                TextView email = (TextView) view.findViewById(R.id.email_list_view_item);
                Button mailButton = (Button) view.findViewById(R.id.send_email_button);
                ArrayList<String> emailArray = contact.getEmailAddresses();
                final String emailAddress = emailArray.get(position);
                email.setText(emailAddress);

                 mailButton.setOnClickListener( new View.OnClickListener()
                {
                    public void onClick(View temp)
                    {

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                 "mailto",emailAddress, null));
                          emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
                         startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });

                return view;
            }

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.show_contact, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            switch (id){
                case R.id.action_delete:
                    ContactManager.getInstance(this).deleteContact(contact);
                    finish();
                    return true;
                default:
                    return true;
            }
        }

        protected void onRestart()
        {
            super.onRestart();
            ContactManager temp = ContactManager.getInstance(this);
            temp.refresh();
            contact = temp.getContactDetails(cNum);
            populateItems();
        }

     }


