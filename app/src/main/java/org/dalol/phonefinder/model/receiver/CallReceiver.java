package org.dalol.phonefinder.model.receiver;

import android.content.Context;
import android.util.Log;

import org.dalol.phonefinder.model.service.DetectionService;

import java.util.Date;

/**
 * Created by Filippo-TheAppExpert on 8/18/2015.
 */
public class CallReceiver extends PhoneCallReceiver {

    private static final String TAG = CallReceiver.class.getSimpleName();

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.d(TAG, "PhoneCallReceiver detected onIncomingCallStarted");
        DetectionService.stopDetection(ctx);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(TAG, "PhoneCallReceiver detected onOutgoingCallStarted");
        DetectionService.stopDetection(ctx);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "PhoneCallReceiver detected onIncomingCallEnded");
        DetectionService.startDetection(ctx);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "PhoneCallReceiver detected onOutgoingCallEnded");
        DetectionService.startDetection(ctx);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d(TAG, "PhoneCallReceiver detected onMissedCall");
        DetectionService.startDetection(ctx);
    }

}
