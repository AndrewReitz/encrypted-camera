/*
 * Copyright (C) 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andrewreitz.encryptedcamera.ui.fragment;

import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.andrewreitz.encryptedcamera.BuildConfig;
import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.bus.EncryptionEvent;
import com.andrewreitz.encryptedcamera.di.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.di.annotation.ForActivity;
import com.andrewreitz.encryptedcamera.di.annotation.InternalDecryptedDirectory;
import com.andrewreitz.encryptedcamera.di.annotation.UnlockNotification;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.filesystem.SecureDelete;
import com.andrewreitz.encryptedcamera.sharedpreference.AppPreferenceManager;
import com.andrewreitz.encryptedcamera.ui.activity.BaseActivity;
import com.andrewreitz.encryptedcamera.ui.activity.AboutActivity;
import com.andrewreitz.encryptedcamera.ui.activity.GalleryActivity;
import com.andrewreitz.encryptedcamera.ui.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.ui.dialog.FirstRunDialog;
import com.andrewreitz.encryptedcamera.ui.dialog.PasswordDialog;
import com.andrewreitz.encryptedcamera.ui.dialog.SetPasswordDialog;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
import java.util.List;

import javax.crypto.SecretKey;
import javax.inject.Inject;

import timber.log.Timber;

import static com.andrewreitz.encryptedcamera.bus.EncryptionEvent.EncryptionState.NONE;

public class AppPreferenceFragment extends PreferenceFragment
    implements SetPasswordDialog.SetPasswordDialogListener, Preference.OnPreferenceChangeListener,
    PasswordDialog.PasswordDialogListener, ErrorDialog.ErrorDialogCallback {

  private static final int NOTIFICATION_ID = 1337;

  @Inject NotificationManager notificationManager;
  @Inject KeyManager keyManager;
  @Inject AppPreferenceManager preferenceManager;
  @Inject @UnlockNotification Notification unlockNotification;
  @Inject @EncryptedDirectory File encryptedDirectory;
  @Inject @InternalDecryptedDirectory File unencryptedInternalDirectory;
  @Inject ExternalStorageManager externalStorageManager;
  @Inject EncryptionProvider encryptionProvider;
  @Inject FragmentManager fragmentManager;
  @Inject Bus bus;
  @Inject SecureDelete secureDelete;
  @Inject SecureRandom secureRandom;
  @Inject @ForActivity Context context;

  private SwitchPreference switchPreferenceDecrypt;
  private SwitchPreference switchPreferencePassword;
  private FileCryptographyTask runningTask;
  private EncryptionEvent.EncryptionState encryptionState = NONE;

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    BaseActivity.get(this).inject(this);

    if (!preferenceManager.hasSeenFirstRunFragment()) {
      FirstRunDialog dialog = FirstRunDialog.newInstance();
      dialog.show(fragmentManager, "dialog_first_run");
      preferenceManager.setHasSeenFirstLaunchFragment(true);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.app_preference_screen);
  }

  @Override @SuppressWarnings("ConstantConditions")
  public void onResume() {
    super.onResume();

    bus.register(this);

    switchPreferenceDecrypt =
        (SwitchPreference) findPreference(getString(R.string.pref_key_decrypt));
    switchPreferenceDecrypt.setOnPreferenceChangeListener(this);
    switchPreferencePassword =
        (SwitchPreference) findPreference(getString(R.string.pref_key_use_password));
    switchPreferencePassword.setOnPreferenceChangeListener(this);

    findPreference(getString(R.string.pref_key_version)).setSummary(BuildConfig.VERSION_NAME);
    findPreference(getString(R.string.pref_key_about)).setIntent(
        new Intent(context, AboutActivity.class));

    findPreference(getString(R.string.pref_key_gallery)).setIntent(
        new Intent(context, GalleryActivity.class));
  }

  @Override public void onPause() {
    super.onPause();
    if (runningTask != null) {
      runningTask.getProgressDialog().dismiss();
      runningTask.cancel(false);
    }
    bus.unregister(this);
  }

  @Override public void onPasswordSet(final String password) {
    File appExternalDirectory = getAppExternalDirectory();
    if (appExternalDirectory == null) return;
    executeDecryptFileTask(appExternalDirectory, new FileCryptographyTask.TaskFinishedCallback() {
      @Override public void onSuccess() {
        if (setNewPassword(password)) return;
        File appExternalDirectory = getAppExternalDirectory();
        if (appExternalDirectory == null) return;
        encryptSdDirectory(appExternalDirectory);
      }

      @Override public void onError() {
        Timber.e("error encrypting images after password was set");
      }
    });
  }

  private boolean setNewPassword(String password) {
    byte[] salt = new byte[10];
    secureRandom.nextBytes(salt);
    try {
      SecretKey secretKey = keyManager.generateKeyWithPassword(password.toCharArray(), salt);
      encryptionProvider.setSecretKey(secretKey);
      keyManager.saveKey(EncryptedCameraApp.KEY_STORE_ALIAS, secretKey);
      keyManager.saveKeyStore();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | CertificateException | KeyStoreException | IOException e) {
      Timber.e(e, "Error saving encryption key with password");
      ErrorDialog.newInstance(getString(R.string.encryption_error),
          getString(R.string.error_saving_encryption_key));
      return true;
    }
    preferenceManager.setPassword(password);
    preferenceManager.setSalt(salt);
    preferenceManager.setHasPassword(true);
    switchPreferencePassword.setChecked(true);
    return false;
  }

  @Override public void onPasswordEntered(String password) {
    if (!doPasswordCheck(password)) {
      showIncorrectPasswordDialog();
      return;
    }
    if (!setSecretKey(password)) return;
    decryptToSdDirectory(externalStorageManager.getAppExternalDirectory());
  }

  @Override public void onPasswordCancel() {
    switchPreferenceDecrypt.setChecked(false);
  }

  @Override public void onPasswordSetCancel() {
  }

  @Override public void onErrorDialogDismissed() {
    // TODO Remove All Get Activities and make a manager
    //noinspection ConstantConditions
    getActivity().finish();
  }

  @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
    if (checkNotCurrentlyEncrypting()) return false;

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

  private boolean checkNotCurrentlyEncrypting() {
    if (!encryptionState.equals(NONE)) {
      showErrorDialog(getString(R.string.error),
          EncryptionEvent.EncryptionState.DECRYPTING == encryptionState ? getString(
              R.string.error_currently_decrypting) : getString(R.string.error_currently_encrypting),
          "error_decrypting_in_progress");
      return true;
    }
    return false;
  }

  @SuppressWarnings("UnusedDeclaration") @Subscribe public void handleEncryptionEvent(
      EncryptionEvent event) {
    if (event.state.equals(NONE)) {
      getActivity().setProgressBarIndeterminateVisibility(false);
    } else {
      getActivity().setProgressBarIndeterminateVisibility(true);
    }
    encryptionState = event.state;
  }

  private boolean handleDecryptedPreference(boolean value) {
    handleEncryption(value);
    return false;
  }

  private boolean handleUsePasswordPreference(boolean checked) {
    if (preferenceManager.isDecrypted()) {
      // don't allow changing password while photos are decrypted
      showErrorDialog(getString(R.string.error),
          getString(R.string.error_change_password_while_decrypted),
          "error_change_password_while_decrypted");
      return false;
    } else if (checked
        && !preferenceManager.hasPassword()) { // check if a password has already been set do to the filtering done for passwords
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
        if (!doPasswordCheck(password)) {
          showIncorrectPasswordDialog();
          return;
        }
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
      }
      switchPreferencePassword.setChecked(false);
    } catch (InvalidKeyException | IOException | InvalidAlgorithmParameterException e) {
      Timber.w(e, "Error encrypting files without a password");
      // if this exception really happens the application won't work
      // We should really crash.
      throw new RuntimeException(e);
    }
  }

  private boolean decryptFilesInternally(@NonNull String password) {
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
      showErrorDialog(getString(R.string.error), getString(R.string.error_incorrect_password),
          "error_dialog_removing_password");
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
      showErrorDialog(getString(R.string.encryption_error),
          getString(R.string.error_saving_encryption_key), "error_dialog_generate_key_no_password");
    }

    return false;
  }

  /** true for decrypt, false for encrypt */
  private void handleEncryption(boolean decrypt) {
    File appExternalDirectory = getAppExternalDirectory();
    if (appExternalDirectory == null) return;

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

  /**
   * Would pull this out and just set once, but we need to constantly check due to possibility of
   * changing state
   */
  private File getAppExternalDirectory() {
    File appExternalDirectory = externalStorageManager.getAppExternalDirectory();

    if (appExternalDirectory == null
        || !externalStorageManager.checkSdCardIsInReadAndWriteState()) {
      //noinspection ConstantConditions
      showErrorDialog(getString(R.string.error), getString(R.string.error_sdcard_message),
          "error_dialog_sdcard");
      return null;
    }
    return appExternalDirectory;
  }

  private void decryptToSdDirectory(@NonNull File appExternalDirectory) {
    this.decryptToSdDirectory(appExternalDirectory, null);
  }

  private void decryptToSdDirectory(@NonNull File appExternalDirectory, @Nullable String password) {
    if (password != null && !doPasswordCheck(password)) {
      showIncorrectPasswordDialog();
      return;
    }

    executeDecryptFileTask(appExternalDirectory, new FileCryptographyTask.TaskFinishedCallback() {
      @Override public void onSuccess() {
        notificationManager.notify(NOTIFICATION_ID, unlockNotification);
        switchPreferenceDecrypt.setChecked(true);
      }

      @Override public void onError() {
        // there was an error reset the switch preferences
        switchPreferenceDecrypt.setChecked(false);
      }
    });
  }

  private void executeDecryptFileTask(@NonNull File appExternalDirectory,
      FileCryptographyTask.TaskFinishedCallback callback) {
    //noinspection ConstantConditions
    runningTask = new DecryptFilesTask(appExternalDirectory, encryptionProvider, context,
        ImmutableList.copyOf(encryptedDirectory.listFiles()), callback,
        getString(R.string.decrypting_files), bus);
    //noinspection unchecked
    runningTask.execute();
  }

  private void showIncorrectPasswordDialog() {
    showErrorDialog(getString(R.string.error), getString(R.string.error_incorrect_password),
        "error_dialog_encrypt_pw");
  }

  private void encryptSdDirectory(File appExternalDirectory) {
    executeEncryptFileTask(appExternalDirectory, new FileCryptographyTask.TaskFinishedCallback() {
      @Override public void onSuccess() {
        notificationManager.cancel(NOTIFICATION_ID);
        switchPreferenceDecrypt.setChecked(false);
      }

      @Override public void onError() {
        switchPreferenceDecrypt.setChecked(true);
      }
    });
  }

  private void executeEncryptFileTask(File appExternalDirectory,
      FileCryptographyTask.TaskFinishedCallback callback) {
    //noinspection ConstantConditions
    runningTask =
        new EncryptFilesTask(unencryptedInternalDirectory, encryptedDirectory, secureDelete,
            context, ImmutableList.copyOf(appExternalDirectory.listFiles()), encryptionProvider,
            callback, getString(R.string.encrypting_files), bus);

    runningTask.execute();
  }

  private boolean setSecretKey(String password) {
    try {
      // recreate the secret key and give it to the encryption provider
      SecretKey key =
          keyManager.generateKeyWithPassword(password.toCharArray(), preferenceManager.getSalt());
      encryptionProvider.setSecretKey(key);
      return true;
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      Timber.w(e, "Error recreating secret key.  This probably should never happen");
      showErrorDialog(getString(R.string.error), getString(R.string.error_terrible),
          "error_dialog_recreate_key");
    }

    return false;
  }

  private void showErrorDialog(String error, String message, String tag) {
    showErrorDialog(error, message, tag, null);
  }

  private void showErrorDialog(@Nullable String error, @Nullable String message,
      @Nullable String tag, @Nullable ErrorDialog.ErrorDialogCallback callback) {
    ErrorDialog errorDialog = ErrorDialog.newInstance(error, message);

    if (callback != null) {
      errorDialog.setCallback(callback);
    }

    errorDialog.show(fragmentManager, tag);
  }

  private boolean doPasswordCheck(@NonNull String password) {
    String passwordHash = preferenceManager.getPasswordHash();
    return BCrypt.checkpw(password, passwordHash);
  }

  private static abstract class FileCryptographyTask extends AsyncTask<Void, Void, Boolean> {

    private final Context context;
    private final List<File> files;
    private final TaskFinishedCallback callback;
    private final String progressMessage;
    private final Bus bus;

    private ProgressDialog progressDialog;

    FileCryptographyTask(@NonNull Context context, @NonNull List<File> files,
        @NonNull TaskFinishedCallback callback, @NonNull String progressMessage, @NonNull Bus bus) {
      this.context = context;
      this.files = files;
      this.callback = callback;
      this.progressMessage = progressMessage;
      this.bus = bus;
    }

    @Override final protected void onPreExecute() {
      progressDialog = new ProgressDialog(context);
      progressDialog.setCancelable(true);
      progressDialog.setMessage(progressMessage);
      progressDialog.setCanceledOnTouchOutside(false);
      progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      progressDialog.setProgress(0);
      progressDialog.setMax(getFiles().size());
      progressDialog.show();
    }

    @Override final protected void onProgressUpdate(Void... values) {
      getProgressDialog().setProgress(1 + getProgressDialog().getProgress());
    }

    @Override protected void onCancelled(Boolean success) {
      finishedExecuting(success);
    }

    @Override protected void onPostExecute(Boolean success) {
      if (!isCancelled()) {
        progressDialog.dismiss();
      }
      finishedExecuting(success);
    }

    protected Context getContext() {
      return context;
    }

    protected List<File> getFiles() {
      return files;
    }

    protected ProgressDialog getProgressDialog() {
      return progressDialog;
    }

    protected Bus getBus() {
      return bus;
    }

    private void finishedExecuting(boolean success) {
      getBus().post(new EncryptionEvent(NONE));
      if (success) {
        callback.onSuccess();
      } else {
        callback.onError();
      }
    }

    public interface TaskFinishedCallback {
      void onSuccess();

      void onError();
    }
  }

  private static final class DecryptFilesTask extends FileCryptographyTask {

    private final File appExternalDirectory;
    private final EncryptionProvider encryptionProvider;

    DecryptFilesTask(@NonNull File appExternalDirectory,
        @NonNull EncryptionProvider encryptionProvider, @NonNull Context context,
        @NonNull List<File> files, @NonNull TaskFinishedCallback callback,
        @NonNull String progressMessage, @NonNull Bus bus) {
      super(context, files, callback, progressMessage, bus);
      this.appExternalDirectory = appExternalDirectory;
      this.encryptionProvider = encryptionProvider;
      bus.post(new EncryptionEvent(EncryptionEvent.EncryptionState.DECRYPTING));
    }

    @Override protected Boolean doInBackground(Void... params) {

      boolean success = true;

      for (File encrypted : getFiles()) {
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
          success = false;
        }
        publishProgress();
      }

      return success;
    }
  }

  private final static class EncryptFilesTask extends FileCryptographyTask {
    private final File appInternalUnencryptedDirectory;
    private final File appInternalEncryptedDirectory;
    private final SecureDelete secureDelete;
    private final EncryptionProvider encryptionProvider;

    EncryptFilesTask(@NonNull File appInternalUnencryptedDirectory,
        @NonNull File appInternalEncryptedDirectory, @NonNull SecureDelete secureDelete,
        @NonNull Context context, @NonNull List<File> files,
        @NonNull EncryptionProvider encryptionProvider, @NonNull TaskFinishedCallback callback,
        @NonNull String progressMessage, @NonNull Bus bus) {
      super(context, files, callback, progressMessage, bus);
      this.appInternalEncryptedDirectory = appInternalEncryptedDirectory;
      this.appInternalUnencryptedDirectory = appInternalUnencryptedDirectory;
      this.secureDelete = secureDelete;
      this.encryptionProvider = encryptionProvider;
      bus.post(new EncryptionEvent(EncryptionEvent.EncryptionState.ENCRYPTING));
    }

    @Override protected Boolean doInBackground(Void... params) {
      boolean success = true;

      for (File unencryptedFile : getFiles()) {
        File unencryptedInternal =
            new File(appInternalUnencryptedDirectory, unencryptedFile.getName());
        File encryptedFile = new File(appInternalEncryptedDirectory, unencryptedFile.getName());

        try {
          // Copy the file internally so the user can't mess with it while we are encrypting
          Files.copy(unencryptedFile, unencryptedInternal);

          // File moved internally now delete the original
          secureDelete.secureDelete(unencryptedFile);

          //noinspection ResultOfMethodCallIgnored
          encryptedFile.createNewFile();
          encryptionProvider.encrypt(unencryptedInternal, encryptedFile);

          //noinspection ResultOfMethodCallIgnored
          unencryptedInternal.delete();
        } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
          Timber.e(e, "Error encrypting and saving image");
          success = false;
        }

        publishProgress();
      }

      return success;
    }
  }
}
