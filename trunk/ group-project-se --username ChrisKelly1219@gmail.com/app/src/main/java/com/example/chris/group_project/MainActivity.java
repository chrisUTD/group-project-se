package com.example.chris.group_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be be  st to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private static HashMap<Integer, String> tabNames;

    static {
        tabNames = new HashMap<Integer, String>();
        tabNames.put(0, "Contacts");
        tabNames.put(1, "Groups");
        tabNames.put(2, "Favorites");
    }

    private static int TAB_COUNT = 3;
    private static String TAB_NAME = "tab_name";
    private static Integer currentTabIndex = 0;

    private View currentTabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                tabChanged(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setTitle(tabNames.get(mViewPager.getCurrentItem()));

        ((TextView)findViewById(R.id.tab_button_label_0)).setText("Contacts");
        ((TextView)findViewById(R.id.tab_button_label_1)).setText("Groups");
        ((TextView)findViewById(R.id.tab_button_label_2)).setText("Favorites");

        View tab0 = findViewById(R.id.tab_0);
        View tab1 = findViewById(R.id.tab_1);
        View tab2 = findViewById(R.id.tab_2);

        currentTabView = tab0;

        tab0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabButtonClicked(0);
            }
        });
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabButtonClicked(1);
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabButtonClicked(2);
            }
        });

    }

    public void tabButtonClicked(Integer index){
        if (currentTabIndex != index){
            currentTabIndex = index;
            mViewPager.setCurrentItem(index, true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                //TODO: ???
                return true;
            case R.id.action_add:
                actionAdd();
                return true;
            case R.id.action_search:
                actionSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContactManager.getInstance(this).refresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Main Activity", "returned on activity result");

        ContactManager.getInstance(this).refresh();
    }

    private void actionAdd(){
        switch (mViewPager.getCurrentItem()){
            case 0:
                launchAddContact();
                break;
            case 1:
                launchAddGroup();
                break;
            case 2:
                //TODO: ???
                break;
            default:
                //nothing
        }
    }

    private void actionSearch(){
        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);
        promptBuilder.setTitle("Search Contacts");
        final EditText editText = new EditText(this);
        promptBuilder.setView(editText);
        String defaultText = "Search for...";
        editText.setText(defaultText);
        editText.setSelection(0, defaultText.length());

        final Context activityContext = this;

        promptBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString();
                if (value.length() > 0) {
                    ContactManager.getInstance(activityContext).filterBySearchTerm(value);
                }
            }
        });
        promptBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ContactManager.getInstance(activityContext).unfilter();
            }
        });
        AlertDialog prompt = promptBuilder.create();
        prompt.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        prompt.show();
    }

    private void launchAddContact(){
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra("finishActivityOnSaveCompleted", true);
        startActivity(intent);
    }

    private void launchAddGroup(){
        AlertDialog.Builder promptBuilder = new AlertDialog.Builder(this);
        promptBuilder.setTitle("Add New Group");
        final EditText editText = new EditText(this);
        promptBuilder.setView(editText);
        String defaultText = "Group Name";
        editText.setText(defaultText);
        editText.setSelection(0, defaultText.length());

        final Context activityContext = this;

        promptBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString();
                if (value.length() > 0) {
                    Group newGroup = new Group(value);
                    newGroup = GroupManager.getInstance(getApplicationContext()).insert(new Group(value));

                    Intent displayGroupIntent = new Intent(activityContext, GroupActivity.class);
                    if (newGroup.getId() != -1) {
                        displayGroupIntent.putExtra("groupId", newGroup.getId());
                    }
                    activityContext.startActivity(displayGroupIntent);
                }
            }
        });
        promptBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing.
            }
        });
        AlertDialog prompt = promptBuilder.create();
        prompt.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        prompt.show();
    }

    private void tabChanged(Integer index){
        setTitle(tabNames.get(index));
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment;
            switch(position){
                case 0:
                    fragment = ContactsFragment.newInstance(position);
                    break;
                case 1:
                    fragment = GroupFragment.newInstance(position);
                    break;
                case 2:
                    //TODO: Replace this with another tab item:
                    fragment = ContactsFragment.newInstance(position);
                    break;
                default:
                    fragment = ContactsFragment.newInstance(position);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ContactsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        int section_number;
        String section_name;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ContactsFragment newInstance(int sectionNumber) {
            ContactsFragment fragment = new ContactsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(TAB_NAME, tabNames.get(sectionNumber));
            fragment.setArguments(args);
            return fragment;
        }

        public ContactsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            section_number = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;
            section_name = getArguments() != null ? getArguments().getString(TAB_NAME) : "";
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//            ((TextView)rootView.findViewById(R.id.section_label)).setText(section_name);
            ContactListAdapter adapter = new ContactListAdapter(getActivity(),
                    R.layout.contact_list_item_view,
                    ContactManager.getInstance(getActivity()),
                    ContactListAdapter.ContactListMode.SHOW_DETAILS_ON_CLICK);
            ((ListView)rootView.findViewById(R.id.contacts_list_view)).setAdapter(adapter);

            return rootView;
        }

    }

    /**
     * GROUPS FRAGMENT
     */
    public static class GroupFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        int section_number;
        String section_name;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static GroupFragment newInstance(int sectionNumber) {
            GroupFragment fragment = new GroupFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(TAB_NAME, tabNames.get(sectionNumber));
            fragment.setArguments(args);
            return fragment;
        }

        public GroupFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            section_number = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;
            section_name = getArguments() != null ? getArguments().getString(TAB_NAME) : "";
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_groups_list, container, false);

//            ((TextView)rootView.findViewById(R.id.section_label)).setText(section_name);
            GroupsListAdapter adapter = new GroupsListAdapter(getActivity(),
                    R.layout.group_list_item_view,
                    GroupManager.getInstance(getActivity())
                    );
            ((ListView)rootView.findViewById(R.id.groups_list_view)).setAdapter(adapter);

            return rootView;
        }

    }
}
