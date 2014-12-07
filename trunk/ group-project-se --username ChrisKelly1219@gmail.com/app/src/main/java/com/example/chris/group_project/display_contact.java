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
 * display_contact class for displaying the contact information when a contact is selected
 */

    public class display_contact  extends Activity
    {
        /**
         * optionsMenu holds the options to delete or block/unBlock a contact
         */
        Menu optionsMenu;

        /**
         * This is populated with views that contain a Text and Call button as well as a phone
         * number string
         */
        ListView phoneListView;
        /**
         * This is populated with views that contain an button as well as an email string
         */
        ListView emailListView;
        /**
         * contact is passed in by an intent nd is then assigned in the onCreate method
         */
        Contact contact;
        /**
         * Holds the image URI for showing the contact's assigned picture
         */
        ImageView picture;
        /**
         * Holds a string containing the contact's display name
         */
        TextView name;
        /**
         * Contains a an array of Strings of the contact's phone numbers
         */
        ArrayAdapter<String> phoneNumberAdapter;
        /**
         * Contains a an array of Strings of the contact's email addresses
         */
        ArrayAdapter<String> emailAdapter;
        /**
         * The layout contains button's that are assigned to different groups
         */
        LinearLayout groupHolder;
        /**
         * This contains the contact's number in the android database
         * this is used when the contact is edited and allows the display to update
         * with any changed information
         */
        private static int cNum;


        /**
         * Empty constructor, used when launching activity via intent.
         */
        public display_contact(){}


        /**
         *
         * This method is run on the activity creation
         * populates various views of the contact
         *
         */
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContact();
            setContentView(R.layout.show_contact);
            populateItems();
        }

        /**
         *  Retrieve the contact id passed through the intent when this activity is launched
         * the id is used to set this.contact to the appropriate value
         */
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

        /**
         * Inflates the NameView,ImageView,and the PhoneNumber and Email Address list views
         */
        private void populateItems()
        {
            createNameView();
            if(contact.getPhotoUri() != null)createImageView();
            createEditButton();
            if(contact.getPhoneNumbers() != null){CreatePhoneListView();}
            if(contact.getEmailAddresses() != null)CreateEmailListView();
            populateContactGroupView();
        }

        /**
         * Sets sc_Name to the display name of contact
         */
        private void createNameView()
        {
            name = (TextView) findViewById(R.id.sc_Name);
            name.setText(contact.getDisplayName());
        }

        /**
         * Sets the image view on show_Contact to the assigned picture for the contact
         */
        private void createImageView()
        {
            picture = (ImageView) findViewById(R.id.sc_Image);
            if(contact.getPhotoUri() != null) {
                picture.setImageURI(Uri.parse(contact.getPhotoUri()));
            }
        }

        /**
         * Creates the edit button for the contact and adds an onClickListener
         * that initiates the edit contact activity
         */
        private void createEditButton()
        {
            cNum = contact.getId();
            Button edit = (Button) findViewById(R.id.edit_contact_button);

            edit.setOnClickListener( new View.OnClickListener()
            {
                public void onClick(View temp)
                {
                    Uri lookupUri = ContactsContract.Contacts.getLookupUri
                    (Long.parseLong(contact.getCONTACT_ID()), contact.getLOOKUP_KEY());

                    Intent editIntent = new Intent(Intent.ACTION_EDIT);

                    editIntent.setDataAndType(lookupUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                    editIntent.putExtra("finishActivityOnSaveCompleted", true);

                    startActivityForResult(editIntent,0);
                }
            });

        }

        /**
         * Populates the phoneListView using the phoneNumberListAdapter
         * The views contain a string and two buttons
         */
        private void CreatePhoneListView()
        {
            phoneListView = (ListView) findViewById(R.id.phone_number_listView);
            phoneNumberAdapter = new phoneNumberListAdapter();
            phoneListView.setAdapter((phoneNumberAdapter));
        }

        /**
         * Populates the email_list_view by using the emailListAdapter
         * the views contain a string and one button
         */
        private void CreateEmailListView()
        {
            emailListView = (ListView) findViewById(R.id.email_list_view);
            emailAdapter = new emailListAdapter();
            emailListView.setAdapter((emailAdapter));
        }

        /**
         * Populates the contactGroupView with buttons
         * These buttons display a group name and have an onClickListener
         * that launches a group activity
         */
       private void populateContactGroupView()
       {

           ArrayList<Button> groups = createGroupBtns();
           addGroupButtonsToView(groups);
       }

        /**
         *
         * @return ArrayList of Buttons that display the groups name and have an onClickListener
         * which launches the Group Activity
         */
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

        /**
         * Creates the button with size characteristics and colors
         * These buttons are then passed back to createGroupBtns where they are added to an array
         * @param group
         * @return a button with a custom color and size
         */
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

        /**
         * Adds a listener to each group button
         * this listener starts the Group Activity for the specific group when tapped
         * @param btn Button
         * @param temp Group
         * @return A button with with an onClickListener
         */
        private Button addGroupClickListener(Button btn,Group temp)
        {

            final GroupManager manager = GroupManager.getInstance(this);
            final Group group =  manager.get(temp.getId());         // makes sure the reference is correct
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

        /**
         * Creates individual views that only contain a button
         * these views are then added to a horizontal list view
         * The larger list view is contained in a horizontal scroll view
         * This larger list view is designed to hold all group buttons
         * @param groups
         */
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

        /**
         * This class is used to make views that are added to a list view
         * the individual views contain a string and two buttons
         * This class is for populating phoneListView
         */
        private class phoneNumberListAdapter extends ArrayAdapter<String>
        {
            /**
             * calls super() to set information from ArrayAdapter
             */
            public phoneNumberListAdapter()
            {
                super (display_contact.this, R.layout.phone_number_list_view, contact.getPhoneNumbers());
            }

            /**
             * This method adds a string and two buttons to a linear layout
             * The buttons are used for texting and calling respectively
             * It also adds an onClicklistener to each button to complete the view
             * @param position
             * @param view
             * @param parent
             * @return
             */
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
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));

                   }
                });
                return view;
            }

        }

        /**
         * This class is used to make views that are added to a list view
         * the individual views contain a string and one buttons
         * This class is for populating emailListView
         */
        private class emailListAdapter extends ArrayAdapter<String>
        {
            /**
             * calls super() to set information from ArrayAdapter
             */
            public emailListAdapter()
            {
                super (display_contact.this, R.layout.email_list_view, contact.getEmailAddresses());
            }

            /**
             * Creates a layout that contains a string and one button
             * the string contains a n email address and the button has an onClickListerner
             * the listener allows a user to email when tapped
             * @param position
             * @param view
             * @param parent
             * @return
             */
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

        /**
         *
         * Inflate the menu; this adds items to the action bar if it is present.
         * @param menu
         * @return a menu that allows user to block or delete a contact
         */
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.show_contact, menu);
            this.optionsMenu = menu;
            SetBlockMessage();
            return true;
        }

        /**
         * Sets it so when a block contact calls it goes straight to voice mail instead of ringing
         */
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

        /**
         * Does the appropriate action when an item is selected from the options menu
         * @param item
         * @return true when the action has been completed
         */
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

        /**
         * This function repopulates all views after editing a few in the android default
         * contact editor
         */
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


