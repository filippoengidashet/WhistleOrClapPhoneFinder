package org.dalol.phonefinder.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.dalol.phonefinder.model.constant.Constant;
import org.dalol.phonefinder.model.service.DetectionService;
import org.dalol.phonefinder.model.utilities.SharedPreferenceUtils;

/**
 * Starts the Service at boot if it's specified in the preferences.
 *
 * @author Filippo Engidashet
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {

        boolean status = Boolean.parseBoolean(SharedPreferenceUtils.getValue(context, Constant.ENABLE_PREFERENCE));
        if (status) {
            DetectionService.startDetection(context);
        }
    }
}
