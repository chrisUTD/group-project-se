package com.example.chris.group_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adatpter to provide cell views for Groups list view.
 */
public class GroupsListAdapter extends ArrayAdapter<Group> implements View.OnClickListener, ModelChangeListener {
    /**
     * Reference to the group manager that provides the data to display.
     */
    private GroupManager manager;
    /** reference to current context **/
    private Context context;

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
     * Basic constructor
     * @param context current context.
     * @param textViewResourceId layout for the cell view.
     */
    private GroupsListAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    /**
     * Constructor with groups list.
     * @param context current context.
     * @param resource layout for the cell view.
     * @param groups list of groups to display.
     */
    private GroupsListAdapter(Context context, int resource, List<Group> groups){
        super(context, resource, groups);
    }

    /**
     * Constuctor with group manager.
     * @param context current context.
     * @param resource layout for the cell view.
     * @param manager group manager containing the groups to display.
     */
    public GroupsListAdapter(Context context, int resource, GroupManager manager){
        this(context, resource, manager.getGroups());
        this.manager = manager;
        this.context = context;

        manager.registerListener(this);
    }

    /**
     * Method to create and return the cell views for the list view.
     * @param position
     * @param view
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item_view, null);
        }

        Group group = manager.getGroups().get(position);
        if (group != null){
            Bitmap bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.group_person_icon);

            ((TextView)view.findViewById(R.id.contact_list_item_text)).setText(
                     group.getName());

            ((ImageView)view.findViewById(R.id.contact_list_item_image)).setImageBitmap(bitmap);

            view.setTag(group);
            view.setOnClickListener(this);
        }
        return view;
    }

    /**
     * Get count of cells in the list view.
     * @return
     */
    @Override
    public int getCount() {
        return manager.getGroups().size();
    }

    /**
     * Handle click events on the list item views. Launches the GroupActivity for the clicked group.
     * @param view
     */
    @Override
    public void onClick(View view){

        Group group = (Group)view.getTag();
        if (group != null && group.getClass() == Group.class){
            group =  manager.get(group.getId()); // make sure the reference is correct

            Intent displayGroupIntent = new Intent(getContext(), GroupActivity.class);
            if (group != null) {
                displayGroupIntent.putExtra("groupId", group.getId());
            }
            (getContext()).startActivity(displayGroupIntent);
        }
    }
}
