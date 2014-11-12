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


    public class display_contact  extends Activity
    {
        Menu optionsMenu;
        ListView phoneListView;
        ListView emailListView;
        Contact contact;
        ImageView picture;
        TextView name;
        ArrayAdapter<String> phoneNumberAdapter;
        ArrayAdapter<String> emailAdapter;
        LinearLayout groupHolder;
        private static int cNum;


        /**
         * Empty constructor, used when launching activity via intent.
         */
        public display_contact(){}


        // Populates views and sets contact
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContact();
            setContentView(R.layout.show_contact);
            populateItems();
        }


        // Retrieve the contact id passed through the intent when this activity is launched
        // the retrieve contact and set it as this.contact
        private void setContact()
        {
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
            if(contact.getPhotoUri() != null)createImageView();
            createEditButton();
            if(contact.getPhoneNumbers() != null){CreatePhoneListView();}
            if(contact.getEmailAddresses() != null)CreateEmailListView();
            populateContactGroupView();
        }

        // Sets nmae view to the name of the contact
        private void createNameView()
        {
            name = (TextView) findViewById(R.id.sc_Name);
            name.setText(contact.getDisplayName());
        }

        // Sets the image view on show_COntact to the assigned picture for the contact
        private void createImageView()
        {
            picture = (ImageView) findViewById(R.id.sc_Image);

            if(contact.getPhotoUri() != null) {
                picture.setImageURI(Uri.parse(contact.getPhotoUri()));
            }
        }

        //Creates the edit button for the contact and adds an onClickListener
        // that initiates the edit contact activity
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
        //Populates the phone_number_list view by using the PhoneListAdapter
        private void CreatePhoneListView()
        {
            phoneListView = (ListView) findViewById(R.id.phone_number_listView);
            phoneNumberAdapter = new phoneNumberListAdapter();
            phoneListView.setAdapter((phoneNumberAdapter));
        }
        //Populates the email_list_view by using the emailListAdapter
        private void CreateEmailListView()
        {
            emailListView = (ListView) findViewById(R.id.email_list_view);
            emailAdapter = new emailListAdapter();
            emailListView.setAdapter((emailAdapter));
        }

       private void populateContactGroupView()
       {

           ArrayList<Button> groups = createGroupBtns();
           addGroupButtonsToView(groups);
       }

        //This method gets the name of all groups belonging to contact and puts them on buttons
        private ArrayList<Button> createGroupBtns()
        {
            ArrayList<Button> groupBs = new ArrayList<Button>();
            final GroupManager manager = GroupManager.getInstance(this);
            ArrayList<Group> groupHolder = manager.getGroupsForContact(contact);

            for(Group group : groupHolder)
            {
                Button temp = createGroupButton(group);
                groupBs.add(temp);
            }
            return groupBs;
        }
        //Creates the button with size characteristics and colors
        //These buttons are then passed back to createGroupBtns where they are added tpo an array
        private Button createGroupButton(Group group)
        {
            Button btn = new Button(this);
            btn.setText(group.getName());
            btn.setBackgroundColor(-16088125);              // Sets color to a Blue
            btn.setTextColor(-1);                           // Sets text to White
            btn.setPadding(5,0,0,0);
            addGroupClickListener(btn,group);
            return btn;
        }

        //adds the listener to each group button that launches the appropriate group activity
        //when the group is tapped
        private Button addGroupClickListener(Button btn,Group temp)
        {

            final GroupManager manager = GroupManager.getInstance(this);
            final Group group =  manager.get(temp.getId()); // make sure the reference is correct
            btn.setOnClickListener( new View.OnClickListener()
            {
                public void onClick(View view)
                {

                    if (group != null )
                    {
                        Intent displayGroupIntent = new Intent( display_contact.this,GroupActivity.class);

                        if (group != null) {
                            displayGroupIntent.putExtra("groupId", group.getId());
                        }
                        startActivity(displayGroupIntent);
                    }
                }
            });
            return btn;
        }

        //Creates individual views that are then added to a horizontal list view
        //The larger list view is contained in a horizontal scroll view
        private void addGroupButtonsToView(ArrayList<Button> groups)
        {
            groupHolder = (LinearLayout) findViewById(R.id.display_groups);
            groupHolder.removeAllViews();
            for(Button groupBtn : groups)
            {
                groupHolder.addView(
                        groupBtn,
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                );
                groupHolder.addView(
                        new TextView(this),
                        new ViewGroup.LayoutParams(5,ViewGroup.LayoutParams.WRAP_CONTENT)
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

        // Inflate the menu; this adds items to the action bar if it is present.
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.show_contact, menu);
            this.optionsMenu = menu;
            SetBlockMessage();
            return true;
        }

        private void SetBlockMessage()
        {
            if(contact != null && this.optionsMenu != null)
            {
                MenuItem BlockMessage = this.optionsMenu.findItem(R.id.action_block);
                if(contact.getSendToVoicemail() == 0)
                {
                    BlockMessage.setTitle("Block Contact");
                }
                else if(contact.getSendToVoicemail() == 1)
                {
                    BlockMessage.setTitle("UnBlock Contact");
                }
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            switch (id){
                case R.id.action_delete:
                    ContactManager.getInstance(this).deleteContact(contact);
                    finish();
                    //return true;
                case R.id.action_block:
                    if(contact.getSendToVoicemail() == 0)
                    {
                        ContactManager.getInstance(this).blockContact(contact);
                    }
                    else
                    {
                        ContactManager.getInstance(this).unblockContact(contact);
                    }
                    this.SetBlockMessage();
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
            groupHolder.removeAllViews();
            populateItems();
        }

     }


