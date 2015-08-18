package org.dalol.phonefinder.model.receiver;

/**
 * Created by Filippo-TheAppExpert on 8/18/2015.
 */

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public abstract class PhoneCallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int mLastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date mCallStartTime;
    private static boolean mIsIncoming;
    private static String mSavedNumber;  //because the passed incoming is only valid in ringing


    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            mSavedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (mLastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                mIsIncoming = true;
                mCallStartTime = new Date();
                mSavedNumber = number;
                onIncomingCallStarted(context, number, mCallStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (mLastState != TelephonyManager.CALL_STATE_RINGING) {
                    mIsIncoming = false;
                    mCallStartTime = new Date();
                    onOutgoingCallStarted(context, mSavedNumber, mCallStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (mLastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, mSavedNumber, mCallStartTime);
                } else if (mIsIncoming) {
                    onIncomingCallEnded(context, mSavedNumber, mCallStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, mSavedNumber, mCallStartTime, new Date());
                }
                break;
        }
        mLastState = state;
    }
}
