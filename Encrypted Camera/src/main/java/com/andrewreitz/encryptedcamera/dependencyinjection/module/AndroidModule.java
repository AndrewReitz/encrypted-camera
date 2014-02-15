package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.CameraIntent;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EnctypedDirectory;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.MediaFormat;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.encryption.KeyManagerImpl;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManagerImpl;
import com.andrewreitz.encryptedcamera.sharedpreference.DefaultSharedPreferenceService;
import com.andrewreitz.encryptedcamera.sharedpreference.SharedPreferenceService;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
@Module(
        library = true,
        includes = {
                SharedPrefsModule.class,
                EncryptionModule.class
        },
        injects = EncryptedCameraApp.class
)
public class AndroidModule {

    private final EncryptedCameraApp application;

    public AndroidModule(EncryptedCameraApp application) {
        this.application = checkNotNull(application);
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication
     *
     * @ForApplication} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton NotificationManager provideNotificationManager() {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @Singleton
    @MediaFormat DateFormat provideMediaDateFormat() {
        return new SimpleDateFormat(EncryptedCameraApp.MEDIA_OUTPUT_DATE_FORMAT);
    }

    @Provides
    @Singleton
    ExternalStorageManager provideExternalStorageManager(@MediaFormat DateFormat dateFormat) {
        return new ExternalStorageManagerImpl(application, dateFormat);
    }

    @Provides
    @CameraIntent Intent provideCameraIntnet() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    @Provides
    @Singleton
    @EnctypedDirectory File provideEncryptedFileDirectory() {
        File file = new File(application.getFilesDir(), EncryptedCameraApp.ENCRYPTED_DIRECTORY);
        if (!file.exists()) {
            if (file.mkdirs()) {
                Timber.w("Error creating encryption directory: %s", file.toString());
                throw new RuntimeException("Error creating encryption directory: %");
            }
        }
        return file;
    }
}
