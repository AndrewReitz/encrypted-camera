package com.andrewreitz.encryptedcamera.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
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

    private static final String UNENCRYPTED_FILE_PATH = "unencrypted_file_path";
    private static final String ENCRYPT_ACTION = "action_encrypt";

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
        EncryptedCameraApp.get(getApplicationContext()).inject(this);

        switch (intent.getAction()) {
            case ENCRYPT_ACTION:
                handleUnencrypt(intent);
                break;
            default:
                throw new IllegalArgumentException("Unknown action == " + intent.getAction());
        }
    }

    private void handleUnencrypt(Intent intent) {
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

    /**
     * Creates an intent and queues it up to the intent service
     *
     * @param context     the applications context
     * @param unencrypted unencrypted file
     */
    public static void startEncryptAction(@NotNull Context context, @NotNull File unencrypted) {
        Intent intent = new Intent(context.getApplicationContext(), EncryptionIntentService.class);
        intent.setAction(ENCRYPT_ACTION);
        intent.putExtra(UNENCRYPTED_FILE_PATH, checkNotNull(unencrypted));
        context.startService(intent);
    }
}
