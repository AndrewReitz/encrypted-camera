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

package com.andrewreitz.encryptedcamera.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.bus.EncryptionEvent;
import com.andrewreitz.encryptedcamera.di.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.di.annotation.EncryptionErrorNotification;
import com.andrewreitz.encryptedcamera.di.annotation.EncryptionNotification;
import com.andrewreitz.encryptedcamera.di.annotation.InternalDecryptedDirectory;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;
import com.andrewreitz.encryptedcamera.filesystem.SecureDelete;
import com.google.common.io.Files;
import com.squareup.otto.Bus;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.inject.Inject;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

public class EncryptionIntentService extends IntentService {

    private static final String UNENCRYPTED_FILE_PATH = "unencrypted_file_path";
    private static final String ENCRYPT_ACTION = "action_encrypt";

    private static final int NOTIFICATION_ERROR_ID = 1337;
    private static final int NOTIFICATION_ENCRYPTING_ID = 1338;

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Inject EncryptionProvider encryptionProvider;
    @Inject @EncryptedDirectory File encryptedFileDirectory;
    @Inject SecureDelete secureDelete;
    @Inject NotificationManager notificationManager;
    @Inject @EncryptionErrorNotification Notification errorNotification;
    @Inject @InternalDecryptedDirectory File internalDecryptedDirectory;
    @Inject @EncryptionNotification Notification encryptingNotification;
    @Inject Bus bus;

    /**
     * Creates an intent and queues it up to the intent service
     *
     * @param context             the applications context
     * @param unencryptedFilePath unencrypted file path
     */
    public static void startEncryptAction(@NotNull Context context, @NotNull final String unencryptedFilePath) {
        Intent intent = new Intent(context.getApplicationContext(), EncryptionIntentService.class);
        intent.setAction(ENCRYPT_ACTION);
        intent.putExtra(UNENCRYPTED_FILE_PATH, checkNotNull(unencryptedFilePath));
        context.startService(intent);
    }

    public EncryptionIntentService() {
        super(EncryptionIntentService.class.getName());
    }

    @Override protected void onHandleIntent(Intent intent) {
        EncryptedCameraApp.get(getApplicationContext()).inject(this);

        switch (intent.getAction()) {
            case ENCRYPT_ACTION:
                handleEncrypt(intent);
                break;
            default:
                throw new IllegalArgumentException("Unknown action == " + intent.getAction());
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(NOTIFICATION_ENCRYPTING_ID);
    }

    private void handleEncrypt(Intent intent) {
        mainThreadHandler.post(new Runnable() {
            @Override public void run() {
                bus.post(new EncryptionEvent(EncryptionEvent.EncryptionState.ENCRYPTING));
            }
        });

        notificationManager.notify(NOTIFICATION_ENCRYPTING_ID, encryptingNotification);
        String serializableExtra = checkNotNull(intent.getStringExtra(UNENCRYPTED_FILE_PATH));

        final File unencryptedFile = new File(serializableExtra);
        File encryptedFile = new File(encryptedFileDirectory, unencryptedFile.getName());
        File unencryptedInternal = new File(internalDecryptedDirectory, encryptedFile.getName());

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
            notificationManager.notify(
                    NOTIFICATION_ERROR_ID,
                    errorNotification
            );
        }

        notificationManager.cancel(NOTIFICATION_ENCRYPTING_ID);
        mainThreadHandler.post(new Runnable() {
            @Override public void run() {
                bus.post(new EncryptionEvent(EncryptionEvent.EncryptionState.NONE));
            }
        });
    }
}
