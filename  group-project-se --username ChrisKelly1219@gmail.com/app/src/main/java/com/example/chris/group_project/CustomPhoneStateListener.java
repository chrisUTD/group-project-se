package com.example.chris.group_project;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by Kyle on 10/27/2014.
 */
public class CustomPhoneStateListener extends PhoneStateListener {

    private Context context;
    public CustomPhoneStateListener(Context context)
    {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber)
    {

        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:

                boolean bBlock = this.NumberIsBlacklisted(incomingNumber);
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                //Turn ON the mute
                audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Class telephonyClass = Class.forName(telephonyManager.getClass().getName());
                    Method method = telephonyClass.getDeclaredMethod("getITelephony");
                    method.setAccessible(true);
                    ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);

                    //Checking incoming call number
                    if (bBlock) {
                        telephonyService = (ITelephony) method.invoke(telephonyManager);
                        telephonyService.silenceRinger();
                        telephonyService.endCall();
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

                //Turn OFF the mute
                audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                break;
        }

        super.onCallStateChanged(state, incomingNumber);
    }

    private boolean NumberIsBlacklisted(String incomingNumber)
    {
        return true;
    }
}
