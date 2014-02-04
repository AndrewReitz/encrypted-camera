package com.andrewreitz.encryptedcamera.fragment;

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.NotificationCompat;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.activity.BaseActivity;
import com.andrewreitz.encryptedcamera.activity.MainActivity;
import com.andrewreitz.encryptedcamera.dialog.SetPasswordDialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;

import dagger.ObjectGraph;
import hugo.weaving.DebugLog;

/**
 * @author Andrew
 */
public class SettingsHomeFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String USE_PASSWORD = "pref_key_use_password";
    public static final String ENCRYPT = "pref_key_encrypt";
    public static final String DECRYPT = "pref_key_decrypt";

    private static final int NOTIFICATION_ID = 1337;

    private ObjectGraph activityGraph;

    @Inject
    NotificationManager notificationManager;

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
                    //setting a password, get a password
                    FragmentManager fm = getFragmentManager();
                    SetPasswordDialog setPasswordDialog = SetPasswordDialog.newInstance();
                    setPasswordDialog.show(fm, "password_dialog");
                } else {
                    //taking off the password need to verify old password and unencrypt all files with password
                }
                break;
            case ENCRYPT:
                // get the setting and then save it into our preference manager
                //preferenceManager.setShouldEncrypt(sharedPreferences.getBoolean(ENCRYPT, false));

                // Create a keystore for encryption that does not require a password


                break;
            case DECRYPT:
                handleDecrypt(sharedPreferences, key);
                break;
        }
    }

    private void handleDecrypt(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences.getBoolean(key, false)) {
            this.notificationManager.notify(
                    NOTIFICATION_ID,
                    getUnlockedNotification()
            );

            // TODO Decrypt
        } else {
            this.notificationManager.cancel(NOTIFICATION_ID);

            // TODO Encrypt
        }
    }

    private Notification getUnlockedNotification() {
        Notification notification = new NotificationCompat.Builder(getActivity())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.images_unencryped_message))
                .setContentIntent(
                        PendingIntent.getActivity(
                                getActivity(),
                                0,
                                new Intent(
                                        getActivity(),
                                        MainActivity.class
                                ),
                                0
                        )
                )
                .setSmallIcon(R.drawable.ic_unlocked)
                .build();

        notification.flags |= Notification.FLAG_NO_CLEAR;
        return notification;
    }
}
