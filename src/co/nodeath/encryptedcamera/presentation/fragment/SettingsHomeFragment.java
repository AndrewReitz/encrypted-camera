package co.nodeath.encryptedcamera.presentation.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import co.nodeath.encryptedcamera.R;

/**
 * @author Andrew
 */
public class SettingsHomeFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_home);
    }
}
