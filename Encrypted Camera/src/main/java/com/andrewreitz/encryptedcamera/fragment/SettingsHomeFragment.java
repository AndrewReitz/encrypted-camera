package com.andrewreitz.encryptedcamera.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.activity.BaseActivity;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.UnlockNotification;
import com.andrewreitz.encryptedcamera.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.dialog.SetPasswordDialog;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.filesystem.SecureDelete;
import com.andrewreitz.encryptedcamera.sharedpreference.EncryptedCameraPreferenceManager;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrew
 */
public class SettingsHomeFragment extends PreferenceFragment implements
        SetPasswordDialog.SetPasswordDialogListener, Preference.OnPreferenceChangeListener {

    private static final int NOTIFICATION_ID = 1337;

    @Inject NotificationManager notificationManager;
    @Inject KeyManager keyManager;
    @Inject EncryptedCameraPreferenceManager preferenceManager;
    @Inject @UnlockNotification Notification unlockNotification;
    @Inject @EncryptedDirectory File encrtypedDirectory;
    @Inject ExternalStorageManager externalStorageManager;
    @Inject EncryptionProvider encryptionProvider;
    @Inject SecureDelete secureDelete;

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

    @Override
    public void onPasswordSet(String password) {
        byte[] salt = new byte[10];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        try {
            keyManager.generateKeyWithPassword(password.toCharArray(), salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Timber.e(e, "Error saving encryption key with password");
            ErrorDialog.newInstance(getString(R.string.encryption_error), getString(R.string.error_saving_encryption_key));
            return;
        }
        preferenceManager.setSalt(salt);
        preferenceManager.setHasPassword(true);
        //noinspection ConstantConditions
        ((SwitchPreference) findPreference(getString(R.string.pref_key_use_password))).setChecked(true);
    }

    @Override
    public void onPasswordCancel() {
        // D/C
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //newValue should always be a boolean but just to be sure
        if (!Boolean.class.isInstance(newValue)) {
            throw new IllegalArgumentException("newValue is not a boolean");
        }

        boolean value = (boolean) newValue;

        //noinspection ConstantConditions
        if (preference.getKey().equals(getString(R.string.pref_key_use_password))) {
            if (value) {
                SetPasswordDialog setPasswordDialog = SetPasswordDialog.newInstance(this);
                //noinspection ConstantConditions
                setPasswordDialog.show(getFragmentManager(), "password_dialog");
                return false;
            } else {
                // TODO: Get password to unencrypt files that were already there
                createKeyNoPassword();
            }
        } else if (preference.getKey().equals(getString(R.string.pref_key_decrypt))) {
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
            Timber.e(e, "Error saving encryption key, no password");
            ErrorDialog.newInstance(getString(R.string.encryption_error), getString(R.string.error_saving_encryption_key));
        }
    }

    private void handleDecrypt(boolean decrypt) {
        File appExternalDirectory = externalStorageManager.getAppExternalDirectory();

        if (appExternalDirectory == null || externalStorageManager.checkSdCardIsInReadAndWriteState()) {
            //noinspection ConstantConditions
            ErrorDialog.newInstance(
                    getString(R.string.error),
                    getString(R.string.error_sdcard_message)
            ).show(getFragmentManager(), "error_dialog_sdcard");
            return;
        }

        if (decrypt) {
            decryptToSdDirectory(appExternalDirectory);
        } else {
            encryptSdDirectory(appExternalDirectory);
        }
    }

    private void decryptToSdDirectory(File appExternalDirectory) {

        if(preferenceManager.hasPassword()) {

        }

        this.notificationManager.notify(
                NOTIFICATION_ID,
                unlockNotification
        );
        //noinspection ConstantConditions
        for (File encrypted : encrtypedDirectory.listFiles()) {
            File unencrypted = new File(appExternalDirectory, encrypted.getName());
            try {
                encryptionProvider.decrypt(encrypted, unencrypted);
                //noinspection ResultOfMethodCallIgnored
                encrypted.delete();
            } catch (InvalidKeyException | IOException | InvalidAlgorithmParameterException e) {
                Timber.w(e, "unable to decrypt and move file to sdcard");
                ErrorDialog errorDialog = ErrorDialog.newInstance(
                        getString(R.string.error),
                        getString(R.string.error_unable_to_decrypt_to_sd)

                );
                //noinspection ConstantConditions
                errorDialog.show(getFragmentManager(), "error_dialog_encrypt");
            }
        }
    }

    private void encryptSdDirectory(File appExternalDirectory) {
        this.notificationManager.cancel(NOTIFICATION_ID);
        // FIX ME check if we need a password
        //noinspection ConstantConditions
        for (File unencrypted : appExternalDirectory.listFiles()) {
            File encrypted = new File(encrtypedDirectory, unencrypted.getName());
            try {
                encryptionProvider.encrypt(unencrypted, encrypted);
                secureDelete.secureDelete(unencrypted);
            } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                Timber.w(e, "unable to encrypt and move file to internal storage");
                ErrorDialog errorDialog = ErrorDialog.newInstance(
                        getString(R.string.error),
                        String.format(getString(R.string.error_reencrypting), unencrypted.getPath())

                );
                //noinspection ConstantConditions
                errorDialog.show(getFragmentManager(), "error_dialog_reencrypt");
            }
        }
    }
}
