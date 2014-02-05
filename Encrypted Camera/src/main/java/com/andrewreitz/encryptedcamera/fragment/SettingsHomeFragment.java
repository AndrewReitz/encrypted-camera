package com.andrewreitz.encryptedcamera.fragment;

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.NotificationCompat;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.activity.BaseActivity;
import com.andrewreitz.encryptedcamera.activity.MainActivity;
import com.andrewreitz.encryptedcamera.dialog.SetPasswordDialog;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.sharedpreference.ObscuredSharedPreferences;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.inject.Named;

import dagger.ObjectGraph;
import hugo.weaving.DebugLog;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrew
 */
public class SettingsHomeFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String USE_PASSWORD = "pref_key_use_password";
    public static final String DECRYPT = "pref_key_decrypt";

    private static final int NOTIFICATION_ID = 1337;

    private ObjectGraph activityGraph;

    @Inject
    NotificationManager notificationManager;

    @Inject
    KeyManager keyManager;

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
    public void onDestroy() {
        // Eagerly clear the reference to the activity graph to allow it to be garbage collected as
        // soon as possible.
        activityGraph = null;

        super.onDestroy();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @DebugLog
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case USE_PASSWORD:
                if (sharedPreferences.getBoolean(key, false)) {
                    FragmentManager fm = getFragmentManager();
                    SetPasswordDialog setPasswordDialog = SetPasswordDialog.newInstance();
                    setPasswordDialog.show(fm, "password_dialog");
                } else {
                    createKeyNoPassword();
                }
                break;
            case DECRYPT:
                handleDecrypt(sharedPreferences, key);
                break;
        }
    }

    private void createKeyNoPassword() {
        // Create a keystore for encryption that does not require a password
        try {
            SecretKey secretKey = keyManager.generateKeyNoPassword();
            keyManager.saveKey(EncryptedCameraApp.KEY_STORE_ALIAS, secretKey);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDecrypt(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences.getBoolean(key, false)) {
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
