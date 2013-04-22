package co.nodeath.encryptedcamera.presentation.fragment;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import co.nodeath.encryptedcamera.R;
import co.nodeath.encryptedcamera.business.service.SharedPreferenceService;
import co.nodeath.encryptedcamera.presentation.dialog.SetPasswordDialog;

/**
 * @author Andrew
 */
public class SettingsHomeFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_home);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SharedPreferenceService.KEY_USE_PASSWORD)) {
            if (sharedPreferences.getBoolean(key, false)) {
                //setting a password, get a password
                FragmentManager fm = getFragmentManager();
                SetPasswordDialog setPasswordDialog = SetPasswordDialog.NewInstance();
                setPasswordDialog.show(fm, "password_dialog");
            } else {
                //taking off the password need to verify old password and unencrypt all files with password
            }
        }
    }
}
