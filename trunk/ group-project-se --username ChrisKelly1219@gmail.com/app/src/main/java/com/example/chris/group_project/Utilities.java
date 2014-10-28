package com.example.chris.group_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

/**
 * Created by Jonny on 10/27/14.
 */
public class Utilities {
    /**
     * Given an Android URI, retrieves the bitmap identified by the URI, if it exists
     * @param context UI context (e.g. the current activity)
     * @param uri the android system URI for the image
     * @return bitmap or null
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri){
        InputStream input;
        try {
            input = context.getContentResolver().openInputStream(uri);
        }
        catch (java.io.FileNotFoundException e){
            return null;
        }
        if (input == null){
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }

}
