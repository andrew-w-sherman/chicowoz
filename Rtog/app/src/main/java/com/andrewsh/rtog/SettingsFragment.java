package com.andrewsh.rtog;

import android.preference.PreferenceFragment;
import android.os.Bundle;

/**
 * Created by Andrew on 7/22/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
