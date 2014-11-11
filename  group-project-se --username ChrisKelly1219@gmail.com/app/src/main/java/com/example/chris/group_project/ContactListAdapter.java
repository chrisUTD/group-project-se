package com.example.chris.group_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * List Adapter subclass that handles the listing of contacts contained in a ContactManager object.
 * This class supports two modes identified with the enumeration SHOW_DETAILS_ON_CLICK and
 * SELECT_ON_CLICK. The former launches the display_contact activity on click for the clicked
 * contact. The latter selects the row and adds it's index to the selectedItems array. This mode
 * allows the list view that the adapter backs to acts as a Contact Picker. Default mode is
 * SHOW_DETAILS_ON_CLICK
 */
public class ContactListAdapter extends ArrayAdapter<Contact> implements View.OnClickListener, ModelChangeListener {

    /**
     * Enum to support the "mode" that the listview can be in, principally whether clicking on a list
     * item launches that contact's view details activity or selects the list item cell so that the
     * activity can return that information in order to provide contact picker functionality.
     */
    public enum ContactListMode {
        SHOW_DETAILS_ON_CLICK,
        SELECT_ON_CLICK
    }

    /**
     * Reference to the ContactManager object that acts as the adapter's data source.
     */
    private ContactManager manager;
    /**
     * Reference to the activity containing the list view associated with the adapter.
     */
    private Context context;
    /**
     * Enumeration SHOW_DETAILS_ON_CLICK and SELECT_ON_CLICK specifying the mode that the list view
     * is in.
     * The former launches the display_contact activity on click for the clicked contact. The latter
     * selects the row and adds it's index to the selectedItems array. This mode allows the list view
     * that the adapter backs to acts as a Contact Picker.
     */
    private ContactListMode mode = ContactListMode.SHOW_DETAILS_ON_CLICK;
    /**
     * Array that hold indices of selected rows when the adapter is in picker mode.
     */
    private static ArrayList<Integer> selectedItems;

    /************************** MODEL CHANGE LISTENER IMPLEMENTATION ******************************/
    private ArrayList<ModelChangeNotifier> notifiers = new ArrayList<ModelChangeNotifier>();

    public void onModelChange(ModelChangeNotifier model) {
        notifyDataSetChanged();
    }

    public void unregister() {
        for (ModelChangeNotifier n : notifiers) {
            if (n != null) {
                n.unregisterListener(this);
            }
        }
    }
    /**********************************************************************************************/

    /**
     * Standard constructor.
     * @param context current context
     * @param textViewResourceId layout for cell views
     */
    private ContactListAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    /**
     * Constructor with list of contacts to display.
     * @param context current context
     * @param resource layout for cell views
     * @param contacts list of contact model objects
     */
    private ContactListAdapter(Context context, int resource, List<Contact> contacts){
        super(context, resource, contacts);
    }

    /**
     * Constructor with ContactManager.
     * @param context current context
     * @param resource layout for cell views
     * @param manager ContactManager object that provides a reference to the list of contacts to be
     *                displayed in the list view.
     */
    public ContactListAdapter(Context context, int resource, ContactManager manager){
        this(context, resource, manager.contacts());
        this.manager = manager;
        this.context = context;
        if (selectedItems == null) {
            this.selectedItems = new ArrayList<Integer>();
        }

        manager.registerListener(this);
    }

    /**
     * Constructor with ContactManager and ContactListMode
     * @param context current context
     * @param resource layout for cell views
     * @param manager ContactManager object that provides a reference to the list of contacts to be
     *                displayed in the list view.
     * @param mode ConctactListMode to determine whether the adapter should treat its list view as a
     *             contact detail view launcher or a picker view.
     */
    public ContactListAdapter(Context context, int resource, ContactManager manager, ContactListMode mode){
        this(context, resource, manager);
        setMode(mode);
    }

    @Override
    public int getCount() {
        return manager.contacts().size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item_view, null);
        }

        Contact contact = manager.contacts().get(position);
        if (contact != null){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.person_icon);

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

    /**
     * Set the mode of the adapter and it's list view -- whether in detail selection mode or picker
     * mode.
     * @param mode ConctactListMode enumeration.
     */
    public void setMode(ContactListMode mode){
        this.mode = mode;
    }

    public boolean isItemSelected(int position){
        if (selectedItems.contains(position)){
            return true;
        }
        return false;
    }

    /**
     * Adds the position to list of selected indices.
     * @param position to add to list.
     */
    private void selectItem(int position){
        selectedItems.add(position);
    }

    /**
     * Removes the position from the list of selected indices.
     * @param position to remove from list.
     */
    private void deselectItem(int position){
        selectedItems.remove(selectedItems.indexOf(position));
    }

    /**
     * Get selected indices.
     * @return reference to list of indices.
     */
    public ArrayList<Integer> getSelectedItems(){
        return selectedItems;
    }

    /**
     * Clears all selected row indices.
     */
    public void unselectAllItems(){
        selectedItems.clear();
    }
}
