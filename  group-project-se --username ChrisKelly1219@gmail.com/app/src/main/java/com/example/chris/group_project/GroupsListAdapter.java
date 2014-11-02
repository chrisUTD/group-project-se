package com.example.chris.group_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jonny on 10/27/14.
 */
public class GroupsListAdapter extends ArrayAdapter<Group> implements View.OnClickListener {

    private GroupManager manager;
    private Context context;

    private GroupsListAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    private GroupsListAdapter(Context context, int resource, List<Group> groups){
        super(context, resource, groups);
    }

    public GroupsListAdapter(Context context, int resource, GroupManager manager){
        this(context, resource, manager.getGroups());
        this.manager = manager;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item_view, null);
        }

        Group group = manager.getGroups().get(position);
        if (group != null){
            Bitmap bitmap = null;

            ((TextView)view.findViewById(R.id.contact_list_item_text)).setText(
                     group.getName());

            view.setTag(group);
            view.setOnClickListener(this);
        }
        return view;
    }

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
