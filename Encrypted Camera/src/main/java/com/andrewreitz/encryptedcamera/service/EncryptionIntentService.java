package com.andrewreitz.encryptedcamera.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EncryptionErrorNotification;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;
import com.andrewreitz.encryptedcamera.filesystem.SecureDelete;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.inject.Inject;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
public class EncryptionIntentService extends IntentService {

    public static final String UNENCRYPTED_FILE_PATH = "unencrypted_file_path";

    private static final int NOTIFICATION_ERROR_ID = 1337;

    @Inject EncryptionProvider encryptionProvider;
    @Inject @EncryptedDirectory File encryptedFileDirectory;
    @Inject SecureDelete secureDelete;
    @Inject NotificationManager notificationManager;
    @Inject @EncryptionErrorNotification Notification errorNotification;

    public EncryptionIntentService() {
        super(EncryptionIntentService.class.getName());
    }

    @Override protected void onHandleIntent(Intent intent) {
        Serializable serializableExtra = checkNotNull(intent.getSerializableExtra(UNENCRYPTED_FILE_PATH));
        if (!(serializableExtra instanceof File)) {
            throw new IllegalArgumentException("intent must pass in a file");
        }

        File unencryptedFile = (File) serializableExtra;
        File encryptedFile = new File(encryptedFileDirectory, unencryptedFile.getName());
        try {
            //noinspection ResultOfMethodCallIgnored
            encryptedFile.createNewFile();
            encryptionProvider.encrypt(unencryptedFile, encryptedFile);
            // File encrypted now delete the original
            secureDelete.secureDelete(unencryptedFile);
        } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            Timber.e(e, "Error encrypting and saving image");
            notificationManager.notify(
                    NOTIFICATION_ERROR_ID,
                    errorNotification
            );
        }
    }

    public static Intent create(@NotNull Context context, @NotNull File unencrypted) {
        Intent intent = new Intent(context.getApplicationContext(), EncryptionIntentService.class);
        intent.putExtra(UNENCRYPTED_FILE_PATH, checkNotNull(unencrypted));
        return intent;
    }
}
