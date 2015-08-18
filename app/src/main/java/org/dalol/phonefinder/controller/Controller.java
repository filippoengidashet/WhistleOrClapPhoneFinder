package org.dalol.phonefinder.controller;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import org.dalol.phonefinder.R;
import org.dalol.phonefinder.model.constant.Constant;
import org.dalol.phonefinder.model.helper.DetectorThread;
import org.dalol.phonefinder.model.helper.RecorderThread;
import org.dalol.phonefinder.model.service.DetectionService;
import org.dalol.phonefinder.model.service.SoundNotificationService;
import org.dalol.phonefinder.model.utilities.SharedPreferenceUtils;
import org.dalol.phonefinder.view.activity.MainActivity;

/**
 * Created by Filippo-TheAppExpert on 8/10/2015.
 */
public class Controller {

    private ControllerListener mListener;

    public Controller(ControllerListener listener) {
        mListener = listener;
    }

    public void rate() {
        String url;
        try {
            mListener.getMainControllerContext().getPackageManager().getPackageInfo("com.android.vending", 0);
            url = "market://details?id=" + Constant.PACKAGE_NAME;
        } catch (final Exception e) {
            url = Constant.LINK_TO_APP;
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mListener.start(intent);
    }

    public void share() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey check out Apk Digger App at: https://play.google.com/store/apps/details?id=" + Constant.PACKAGE_NAME;
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "APK Digger Download Link");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        mListener.start(sharingIntent);
    }

    public void showAbout() {

        View dialog = LayoutInflater.from(mListener.getMainControllerContext().getApplicationContext()).inflate(R.layout.about_layout, null);

        Button sendEmail = (Button) dialog.findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"filippo.eng@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                mListener.start(Intent.createChooser(emailIntent, "Send Email Message.."));
            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(mListener.getMainControllerContext())
                .setCancelable(true)
                .setView(dialog)
                .create();
        Button close = (Button) dialog.findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.show();
    }

    public void browseCode() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Constant.GITHUB_LINK));
        mListener.start(intent);
    }

    public synchronized void startDetection() {
        boolean status = Boolean.parseBoolean(SharedPreferenceUtils.getValue(mListener.getMainControllerContext(), Constant.ENABLE_PREFERENCE));
        if (status) {
            DetectionService.startDetection(mListener.getMainControllerContext());;
        }
    }

    public void stopDetection() {
        DetectionService.stopDetection(mListener.getMainControllerContext());
    }

    public void selectNotificationSound() {
        mListener.selectNotificationSound();
    }

    public void help() {
        mListener.help();
    }

    public interface ControllerListener {

        MainActivity getMainControllerContext();

        void start(Intent intent);

        void help();

        void selectNotificationSound();
    }
}
