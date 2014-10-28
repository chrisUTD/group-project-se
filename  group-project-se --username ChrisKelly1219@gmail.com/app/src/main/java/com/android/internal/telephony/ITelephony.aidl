package com.android.internal.telephony;

/**
 * Created by Kyle on 10/27/2014.
 */
public interface ITelephony {

    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}