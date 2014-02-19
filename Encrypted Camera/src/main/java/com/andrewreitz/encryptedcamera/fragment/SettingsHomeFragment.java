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
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.UnlockNotification;
import com.andrewreitz.encryptedcamera.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.dialog.PasswordDialog;
import com.andrewreitz.encryptedcamera.dialog.SetPasswordDialog;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.filesystem.SecureDelete;
import com.andrewreitz.encryptedcamera.sharedpreference.EncryptedCameraPreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jraf.android.backport.switchwidget.SwitchPreference;
import org.mindrot.jbcrypt.BCrypt;

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

import timber.log.Timber;

/**
 * @author Andrew
 */
public class SettingsHomeFragment extends PreferenceFragment implements
        SetPasswordDialog.SetPasswordDialogListener, Preference.OnPreferenceChangeListener, PasswordDialog.PasswordDialogListener {

    private static final int NOTIFICATION_ID = 1337;

    @Inject NotificationManager notificationManager;
    @Inject KeyManager keyManager;
    @Inject EncryptedCameraPreferenceManager preferenceManager;
    @Inject @UnlockNotification Notification unlockNotification;
    @Inject @EncryptedDirectory File encryptedDirectory;
    @Inject ExternalStorageManager externalStorageManager;
    @Inject EncryptionProvider encryptionProvider;
    @Inject SecureDelete secureDelete;
    @Inject FragmentManager fragmentManager;

    private SwitchPreference switchPreferenceDecrypt;
    private SwitchPreference switchPreferencePassword;

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
        switchPreferenceDecrypt = (SwitchPreference) findPreference(getString(R.string.pref_key_decrypt));
        switchPreferenceDecrypt.setOnPreferenceChangeListener(this);
        switchPreferencePassword = (SwitchPreference) findPreference(getString(R.string.pref_key_use_password));
        switchPreferencePassword.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onPasswordSet(String password) {
        byte[] salt = new byte[10];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        try {
            SecretKey secretKey = keyManager.generateKeyWithPassword(password.toCharArray(), salt);
            encryptionProvider.setSecretKey(secretKey);
            keyManager.saveKey(EncryptedCameraApp.KEY_STORE_ALIAS, secretKey);
            keyManager.saveKeyStore();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | CertificateException | KeyStoreException | IOException e) {
            Timber.e(e, "Error saving encryption key with password");
            ErrorDialog.newInstance(getString(R.string.encryption_error), getString(R.string.error_saving_encryption_key));
            return;
        }
        preferenceManager.setPassword(password);
        preferenceManager.setSalt(salt);
        preferenceManager.setHasPassword(true);
        switchPreferencePassword.setChecked(true);
    }

    @Override public void onPasswordEntered(String password) {
        if (doPasswordCheck(password)) {
            showIncorrectPasswordDialog();
            return;
        }
        if (!setSecretKey(password)) return;
        decryptToSdDirectory(externalStorageManager.getAppExternalDirectory());
    }

    private void showIncorrectPasswordDialog() {
        showErrorDialog(
                getString(R.string.error),
                getString(R.string.error_incorrect_password),
                "error_dialog_encrypt_pw"
        );
    }

    @Override public void onPasswordCancel() {
        switchPreferenceDecrypt.setChecked(false);
    }

    @Override public void onPasswordSetCancel() {
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
            return handleUsePasswordPreference(value);
        } else if (preference.getKey().equals(getString(R.string.pref_key_decrypt))) {
            return handleDecryptedPreference(value);
        }

        throw new RuntimeException("Unknown preference passed in preference == " + preference.getKey());
    }

    private boolean handleDecryptedPreference(boolean value) {
        handleDecrypt(value);
        return false;
    }

    private boolean handleUsePasswordPreference(boolean checked) {
        if (preferenceManager.getDecrypted()) {
            // don't allow changing password while photos are decrypted
            showErrorDialog(
                    getString(R.string.error),
                    getString(R.string.error_change_password_while_decrypted),
                    "error_change_password_while_decrypted"
            );
            return false;
        } else if (checked && !preferenceManager.hasPassword()) { // check if a password has already been set do to the filtering done for passwords
            SetPasswordDialog.newInstance(this).show(fragmentManager, "password_dialog");
            return false;
        } else {
            if (preferenceManager.hasPassword()) {
                turnOffPassword();
                return false;
            } else {
                createKeyNoPassword();
                return true;
            }
        }
    }

    private void turnOffPassword() {
        PasswordDialog.newInstance(new PasswordDialog.PasswordDialogListener() {
            // Create custom because one in activity does not meet our needs
            @Override public void onPasswordEntered(String password) {
                if (!setSecretKey(password)) return;
                if (decryptFilesInternally(password)) return;
                reEncryptFilesInternally();
            }

            @Override public void onPasswordCancel() {
            }
        }).show(fragmentManager, "get_password_dialog");
    }

    private void reEncryptFilesInternally() {
        try {
            //noinspection ConstantConditions
            for (File in : encryptedDirectory.listFiles()) {
                File out = new File(in.getPath().replace(".tmp", ""));
                encryptionProvider.encrypt(in, out);
                //noinspection ResultOfMethodCallIgnored
                in.delete();
                switchPreferencePassword.setChecked(false);
            }
        } catch (InvalidKeyException | IOException | InvalidAlgorithmParameterException e) {
            Timber.w(e, "Error encrypting files without a password");
            // if this exception really happens the application won't work
            // We should really crash.
            throw new RuntimeException(e);
        }
    }

    private boolean decryptFilesInternally(@NotNull String password) {
        if (!doPasswordCheck(password)) {
            if (!doPasswordCheck(password)) {
                showIncorrectPasswordDialog();
                return false;
            }
        }

        try {
            //noinspection ConstantConditions
            for (File in : encryptedDirectory.listFiles()) {
                File out = new File(encryptedDirectory, in.getName() + ".tmp");
                encryptionProvider.decrypt(in, out);
                //noinspection ResultOfMethodCallIgnored
                in.delete();
            }
        } catch (InvalidKeyException | IOException | InvalidAlgorithmParameterException e) {
            Timber.w(e, "error unencrypting internally");
            showErrorDialog(
                    getString(R.string.error),
                    getString(R.string.error_incorrect_password),
                    "error_dialog_removing_password"
            );
            return true;
        }

        return false;
    }

    private boolean createKeyNoPassword() {
        // Create a keystore for encryption that does not require a password
        try {
            SecretKey secretKey = keyManager.generateKeyNoPassword();
            encryptionProvider.setSecretKey(secretKey);
            keyManager.saveKey(EncryptedCameraApp.KEY_STORE_ALIAS, secretKey);
            keyManager.saveKeyStore();
            return true;
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException e) {
            // The app really wouldn't work at this point
            Timber.e(e, "Error saving encryption key, no password");
            showErrorDialog(
                    getString(R.string.encryption_error),
                    getString(R.string.error_saving_encryption_key),
                    "error_dialog_generate_key_no_password"
            );
        }

        return false;
    }

    private void handleDecrypt(boolean decrypt) {
        File appExternalDirectory = externalStorageManager.getAppExternalDirectory();

        if (appExternalDirectory == null || !externalStorageManager.checkSdCardIsInReadAndWriteState()) {
            //noinspection ConstantConditions
            showErrorDialog(
                    getString(R.string.error),
                    getString(R.string.error_sdcard_message),
                    "error_dialog_sdcard"
            );
            return;
        }

        if (decrypt) {
            if (preferenceManager.hasPassword()) {
                PasswordDialog passwordDialog = PasswordDialog.newInstance(this);
                //noinspection ConstantConditions
                passwordDialog.show(fragmentManager, "dialog_get_password");
            } else {
                decryptToSdDirectory(appExternalDirectory);
            }
        } else {
            encryptSdDirectory(appExternalDirectory);
        }
    }

    private void decryptToSdDirectory(@NotNull File appExternalDirectory) {
        this.decryptToSdDirectory(appExternalDirectory, null);
    }

    private void decryptToSdDirectory(@NotNull File appExternalDirectory, @Nullable String password) {
        if (password != null && !doPasswordCheck(password)) {
            showIncorrectPasswordDialog();
            return;
        }

        boolean errorShown = false;
        //noinspection ConstantConditions
        for (File encrypted : encryptedDirectory.listFiles()) {
            File unencrypted = new File(appExternalDirectory, encrypted.getName());
            try {
                encryptionProvider.decrypt(encrypted, unencrypted);
                //noinspection ResultOfMethodCallIgnored
                encrypted.delete();
            } catch (InvalidKeyException | IOException | InvalidAlgorithmParameterException e) {
                Timber.d(e, "unable to decrypt and move file %s to sdcard", encrypted.getPath());
                // Deleted the file that was put on the sdcard and was not the full file
                //noinspection ResultOfMethodCallIgnored
                unencrypted.delete();
                if (!errorShown) { // stop the error from being show multiple times
                    errorShown = true;
                    showErrorDialog(
                            getString(R.string.error),
                            getString(R.string.error_unable_to_decrypt_to_sd),
                            "error_dialog_encrypt"
                    );
                }
            }
        }

        // Error not shown display the notification
        if (!errorShown) {
            this.notificationManager.notify(
                    NOTIFICATION_ID,
                    unlockNotification
            );
            switchPreferenceDecrypt.setChecked(true);
        } else {
            // there was an error reset the switch preferences
            switchPreferenceDecrypt.setChecked(false);
        }
    }

    private void encryptSdDirectory(File appExternalDirectory) {
        this.notificationManager.cancel(NOTIFICATION_ID);
        //noinspection ConstantConditions
        for (File unencrypted : appExternalDirectory.listFiles()) {
            File encrypted = new File(encryptedDirectory, unencrypted.getName());
            try {
                encryptionProvider.encrypt(unencrypted, encrypted);
                secureDelete.secureDelete(unencrypted);
            } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                Timber.d(e, "unable to encrypt and move file to internal storage");
                showErrorDialog(
                        getString(R.string.error),
                        String.format(getString(R.string.error_reencrypting), unencrypted.getPath()),
                        "error_dialog_re_encrypt"
                );
            }
        }

        switchPreferenceDecrypt.setChecked(false);
    }

    private boolean setSecretKey(String password) {
        try {
            // recreate the secret key and give it to the encryption provider
            SecretKey key = keyManager.generateKeyWithPassword(password.toCharArray(), preferenceManager.getSalt());
            encryptionProvider.setSecretKey(key);
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Timber.w(e, "Error recreating secret key.  This probably should never happen");
            showErrorDialog(
                    getString(R.string.error),
                    getString(R.string.error_terrible),
                    "error_dialog_recreate_key"
            );
        }

        return false;
    }

    private void showErrorDialog(String error, String message, String tag) {
        ErrorDialog.newInstance(
                error,
                message
        ).show(fragmentManager, tag);
    }

    private boolean doPasswordCheck(@NotNull String password) {
        String passwordHash = preferenceManager.getPasswordHash();
        return BCrypt.checkpw(password, passwordHash);
    }
}
