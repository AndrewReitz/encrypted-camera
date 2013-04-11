package co.nodeath.encryptedcamera.presentation.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import co.nodeath.encryptedcamera.R;
import co.nodeath.encryptedcamera.business.service.SharedPreferenceService;

/**
 * @author Andrew
 */
public class MainActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SharedPreferenceService.KEY_USE_PASSWORD)) {
            if (sharedPreferences.getBoolean(key, false)) {
                //password set, get a password
            } else {
                //taking off the password need to verify old password and unencrypt all files with password
            }
        }
    }
}