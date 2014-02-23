package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.content.Context;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.InternalDecryptedDirectory;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.MediaFormat;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManagerImpl;
import com.andrewreitz.encryptedcamera.filesystem.SecureDelete;
import com.andrewreitz.encryptedcamera.filesystem.SecureDeleteImpl;

import java.io.File;
import java.security.SecureRandom;
import java.text.DateFormat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * @author areitz
 */
@SuppressWarnings("UnusedDeclaration")
@Module(
        complete = false,
        library = true
)
public class FileSystemModule {

    @Provides
    @Singleton
    ExternalStorageManager provideExternalStorageManager(@ForApplication Context context, @MediaFormat DateFormat dateFormat) {
        return new ExternalStorageManagerImpl(context, dateFormat);
    }

    @Provides
    @Singleton
    @EncryptedDirectory File provideEncryptedFileDirectory(@ForApplication Context context) {
        File file = new File(context.getFilesDir(), EncryptedCameraApp.ENCRYPTED_DIRECTORY);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Error creating encryption directory: " + file.getAbsolutePath());
            }
        }
        return file;
    }

    /**
     * Placeholder directory for files to be placed in while they are being decrypted. This is due to
     * encryption and decryption being pretty slow on most phones.  This directory is for all decypted
     * files to be written to before moved to the SD card to ensure that the user does not mess with
     * them.
     */
    @Provides
    @Singleton
    @InternalDecryptedDirectory File provideTempInternalFileDirectory(@ForApplication Context context) {
        File file = new File(context.getFilesDir(), EncryptedCameraApp.DECRYPTED_DIRECTORY);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Error creating decrypted directory: " + file.getAbsolutePath());
            }
        }
        return file;
    }

    @Provides
    @Singleton SecureDelete provideSecureDelete(SecureRandom secureRandom) {
        return new SecureDeleteImpl(secureRandom);
    }
}
