package com.example.chris.group_project;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Kyle on 11/8/2014.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");

            for (int i = 0; i < pdusObj.length; i++) {

                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                String message = currentMessage.getDisplayMessageBody();

                Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message);

                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
                ContentResolver resolver = context.getContentResolver();
                Cursor callerCursor = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.SEND_TO_VOICEMAIL}, null, null, null);
                String name = "";
                int SendToVoice = 0;
                if (callerCursor != null) {
                    if (callerCursor.moveToFirst()) {
                        SendToVoice = callerCursor.getInt(callerCursor.getColumnIndex(ContactsContract.PhoneLookup.SEND_TO_VOICEMAIL));

                        if (SendToVoice == 1) {
                            abortBroadcast();
                        }
                    }
                }
                if (callerCursor != null && !callerCursor.isClosed()) {
                    callerCursor.close();
                }
            }
        }
    }
}
