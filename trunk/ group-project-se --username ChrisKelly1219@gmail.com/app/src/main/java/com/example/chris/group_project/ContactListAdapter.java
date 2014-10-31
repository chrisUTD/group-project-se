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

import java.util.List;

/**
 * Created by Jonny on 10/27/14.
 */
public class ContactListAdapter extends ArrayAdapter<Contact> implements View.OnClickListener {

    private ContactManager manager;
    private Context context;

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
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item_view, null);
        }

        Contact contact = manager.get(position);
        if (contact != null){
            Bitmap bitmap = null;

            if (contact.getPhotoUri() != null) {
                bitmap = Utilities.getBitmapFromUri(getContext(), Uri.parse(contact.getPhotoUri()));
            }
            ((ImageView) view.findViewById(R.id.contact_list_item_image)).setImageBitmap(bitmap);

            ((TextView)view.findViewById(R.id.contact_list_item_text)).setText(
                     contact.getDisplayName());

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

            Intent displayContactIntent = new Intent(getContext(), display_contact.class);
            if (contact != null){
                displayContactIntent.putExtra("contactId", contact.getId());
            }
            (getContext()).startActivity(displayContactIntent);
        }
    }
}
