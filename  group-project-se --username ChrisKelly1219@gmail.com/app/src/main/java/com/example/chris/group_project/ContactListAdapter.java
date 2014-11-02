package com.example.chris.group_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonny on 10/27/14.
 */
public class ContactListAdapter extends ArrayAdapter<Contact> implements View.OnClickListener {

    /**
     * Enum to support the "mode" that the listview can be in, principally whether clicking on a list
     * item launches that contact's view details activity or selects the list item cell so that the
     * activity can return that information in order to provide contact picker functionality.
     */
    public enum ContactListMode {
        SHOW_DETAILS_ON_CLICK,
        SELECT_ON_CLICK
    }

    private ContactManager manager;
    private Context context;
    private ContactListMode mode = ContactListMode.SHOW_DETAILS_ON_CLICK;
    private static ArrayList<Integer> selectedItems;
    
    private ContactListAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    private ContactListAdapter(Context context, int resource, List<Contact> contacts){
        super(context, resource, contacts);
    }

    public ContactListAdapter(Context context, int resource, ContactManager manager){
        this(context, resource, manager.contacts());
        this.manager = manager;
        this.context = context;
        if (selectedItems == null) {
            this.selectedItems = new ArrayList<Integer>();
        }
    }

    public ContactListAdapter(Context context, int resource, ContactManager manager, ContactListMode mode){
        this(context, resource, manager);
        setMode(mode);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item_view, null);
        }

        Contact contact = manager.contacts().get(position);
        if (contact != null){
            Bitmap bitmap = null;

            if (contact.getPhotoUri() != null) {
                bitmap = Utilities.getBitmapFromUri(getContext(), Uri.parse(contact.getPhotoUri()));
            }
            ((ImageView) view.findViewById(R.id.contact_list_item_image)).setImageBitmap(bitmap);

            ((TextView)view.findViewById(R.id.contact_list_item_text)).setText(
                     contact.getDisplayName());


            if (mode == ContactListMode.SELECT_ON_CLICK
                    && isItemSelected(position)){
                view.setBackgroundColor(getContext().getResources().getColor(R.color.contact_list_selected_background));
            }
            else {
                view.setBackgroundColor(getContext().getResources().getColor(R.color.contact_list_background));
            }

            view.setTag(contact);
            view.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View view){

        Contact contact = (Contact)view.getTag();
        if (contact != null && contact.getClass() == Contact.class){
            contact =  manager.get(contact.getId()); // make sure the reference is correct

            // CODE TO LAUNCH EDIT SCREEN FOR A CONTACT
//            Uri lookupUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(contact.getCONTACT_ID()), contact.getLOOKUP_KEY());
//
//            Intent editIntent = new Intent(Intent.ACTION_EDIT);
//            editIntent.setDataAndType(lookupUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
//            editIntent.putExtra("finishActivityOnSaveCompleted", true);
//
//            ((Activity)getContext()).startActivityForResult(editIntent, 0);

            if (mode == ContactListMode.SHOW_DETAILS_ON_CLICK) {
                Intent displayContactIntent = new Intent(getContext(), display_contact.class);
                if (contact != null) {
                    displayContactIntent.putExtra("contactId", contact.getId());
                }
                (getContext()).startActivity(displayContactIntent);
            }
            else if (mode == ContactListMode.SELECT_ON_CLICK){
                int indexOfContact = manager.contacts().indexOf(contact);
                if (isItemSelected(indexOfContact)){
                    deselectItem(indexOfContact);
                    view.setBackgroundColor(getContext().getResources().getColor(R.color.contact_list_background));
                }
                else {
                    selectItem(indexOfContact);
                    view.setBackgroundColor(getContext().getResources().getColor(R.color.contact_list_selected_background));
                }
            }
        }
    }

    public void setMode(ContactListMode mode){
        this.mode = mode;
    }

    public boolean isItemSelected(int position){
        if (selectedItems.contains(position)){
            return true;
        }
        return false;
    }

    private void selectItem(int position){
        selectedItems.add(position);
    }

    private void deselectItem(int position){
        selectedItems.remove(selectedItems.indexOf(position));
    }


    public ArrayList<Integer> getSelectedItems(){
        return selectedItems;
    }
}
