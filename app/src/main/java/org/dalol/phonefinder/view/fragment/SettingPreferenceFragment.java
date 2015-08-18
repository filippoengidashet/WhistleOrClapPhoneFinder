package org.dalol.phonefinder.view.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import org.dalol.phonefinder.R;
import org.dalol.phonefinder.controller.Controller;
import org.dalol.phonefinder.model.constant.Constant;
import org.dalol.phonefinder.model.service.DetectionService;
import org.dalol.phonefinder.model.utilities.SharedPreferenceUtils;
import org.dalol.phonefinder.view.activity.MainActivity;

/**
 * Created by Filippo-TheAppExpert on 8/10/2015.
 */
public class SettingPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingPreferenceFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        configPreferences();
    }

    private void configPreferences() {
        CheckBoxPreference enableDetection = (CheckBoxPreference) findPreference(Constant.ENABLE_PREFERENCE);
        boolean status = Boolean.parseBoolean(SharedPreferenceUtils.getValue(getActivity(), Constant.ENABLE_PREFERENCE));
        enableDetection.setChecked(status);
        enableDetection.setOnPreferenceClickListener(this);

        ListPreference detectionType = (ListPreference) findPreference(Constant.DETECTION_TYPE_PREFERENCE);
        detectionType.setSummary(detectionType.getValue());
        detectionType.setOnPreferenceChangeListener(this);

        ListPreference accuracy = (ListPreference) findPreference(Constant.ACCURACY_PREFERENCE);
        accuracy.setSummary(accuracy.getValue());
        accuracy.setOnPreferenceChangeListener(this);

        findPreference(Constant.NOTIFICATION_SOUND_PREFERENCE).setOnPreferenceClickListener(this);
        findPreference(Constant.HELP_PREFERENCE).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case Constant.ENABLE_PREFERENCE:
                CheckBoxPreference enableDetection = (CheckBoxPreference) preference;
                SharedPreferenceUtils.save(getActivity(), Constant.ENABLE_PREFERENCE, String.valueOf(enableDetection.isChecked()));
                if (enableDetection.isChecked()) {
                    DetectionService.startDetection(getActivity());
                } else {
                    DetectionService.stopDetection(getActivity());
                }
                break;
            case Constant.NOTIFICATION_SOUND_PREFERENCE:
                getController().selectNotificationSound();
                break;
            case Constant.HELP_PREFERENCE:
                getController().help();
                break;
            default:
                Toast.makeText(getActivity(), "Other", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String newValue = (String) o;

        switch (preference.getKey()) {
            case Constant.ACCURACY_PREFERENCE:
                updatePreference(preference, newValue);
                break;
            case Constant.DETECTION_TYPE_PREFERENCE:
                SharedPreferenceUtils.save(getActivity(), Constant.DETECTION_TYPE_PREFERENCE, newValue);
                updatePreference(preference, newValue);
                break;
            default:
                Toast.makeText(getActivity(), "Other", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void updatePreference(Preference preference, String newValue) {
        ListPreference detectionTypePreference = (ListPreference) preference;
        detectionTypePreference.setValue(newValue);
        preference.setSummary(newValue);
        DetectionService.stopDetection(getActivity());
        DetectionService.startDetection(getActivity());
    }

    private Controller getController() {
        return getMainActivity().getController();
    }
}