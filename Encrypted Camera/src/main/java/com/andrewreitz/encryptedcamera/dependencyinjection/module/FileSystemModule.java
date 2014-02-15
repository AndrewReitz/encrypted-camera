package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.content.Context;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;
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
                Timber.w("Error creating encryption directory: %s", file.toString());
                throw new RuntimeException("Error creating encryption directory: %");
            }
        }
        return file;
    }

    @Provides
    @Singleton SecureDelete provideSecureDelete(SecureRandom secureRandom) {
        return new SecureDeleteImpl(secureRandom);
    }
}
