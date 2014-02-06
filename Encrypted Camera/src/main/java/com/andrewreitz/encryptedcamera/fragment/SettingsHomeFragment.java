package com.andrewreitz.encryptedcamera.fragment;

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.activity.BaseActivity;
import com.andrewreitz.encryptedcamera.dialog.SetPasswordDialog;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.sharedpreference.EncryptedCameraPreferenceManager;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.inject.Named;

import hugo.weaving.DebugLog;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrew
 */
public class SettingsHomeFragment extends PreferenceFragment implements SetPasswordDialog.SetPasswordDialogListener, Preference.OnPreferenceChangeListener {

    private static final int NOTIFICATION_ID = 1337;

    @Inject
    NotificationManager notificationManager;

    @Inject
    KeyManager keyManager;

    @Inject
    EncryptedCameraPreferenceManager preferenceManager;

    @Inject
    @Named("unlock-notification")
    Notification unlockNotification;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BaseActivity.get(this).inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_home);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onResume() {
        super.onResume();
        findPreference(getString(R.string.pref_key_decrypt)).setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.pref_key_use_password)).setOnPreferenceChangeListener(this);
    }

    @DebugLog
    @Override
    public void onPasswordSet(String password) {
        preferenceManager.setHasPassword(true);
        byte[] salt = new byte[10];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        try {
            keyManager.generateKeyWithPassword(password.toCharArray(), salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // TODO Implement Error Message
            throw new RuntimeException(e);
        }
        preferenceManager.setSalt(new String(salt));
    }

    @Override
    public void onPasswordCancel() {
        // D/C
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //newValue should always be a boolean but just to be sure
        if (!newValue.getClass().isInstance(boolean.class)) {
            throw new IllegalArgumentException("newValue is not a boolean");
        }

        boolean value = (boolean) newValue;

        //noinspection ConstantConditions
        if (preference.getKey().equals(getString(R.string.pref_key_decrypt))) {
            if (value) {
                FragmentManager fm = getFragmentManager();
                SetPasswordDialog setPasswordDialog = SetPasswordDialog.newInstance(this);
                //noinspection ConstantConditions
                setPasswordDialog.show(fm, "password_dialog");
                return false;
            } else {
                createKeyNoPassword();
            }
        } else if (preference.getKey().equals(getString(R.string.pref_key_use_password))) {
            handleDecrypt(value);
        }
        return true;
    }

    private void createKeyNoPassword() {
        // Create a keystore for encryption that does not require a password
        try {
            SecretKey secretKey = keyManager.generateKeyNoPassword();
            keyManager.saveKey(EncryptedCameraApp.KEY_STORE_ALIAS, secretKey);
            keyManager.saveKeyStore();
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException e) {
            // The app really wouldn't work at this point
            // TODO display error message to user
            throw new RuntimeException(e);
        }
    }

    private void handleDecrypt(boolean decrypt) {
        if (decrypt) {
            this.notificationManager.notify(
                    NOTIFICATION_ID,
                    unlockNotification
            );

            // TODO Decrypt
        } else {
            this.notificationManager.cancel(NOTIFICATION_ID);

            // TODO Encrypt
        }
    }


}
