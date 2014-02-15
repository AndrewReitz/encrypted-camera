package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.CameraIntent;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.MediaFormat;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManagerImpl;

import java.io.File;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
@SuppressWarnings("UnusedDeclaration")
@Module(
        library = true,
        includes = {
                SharedPrefsModule.class,
                EncryptionModule.class,
                FileSystemModule.class
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
    @Singleton SecureRandom provideSecureRandom() {
        return new SecureRandom();
    }

    @Provides
    @Singleton
    @MediaFormat DateFormat provideMediaDateFormat() {
        return new SimpleDateFormat(EncryptedCameraApp.MEDIA_OUTPUT_DATE_FORMAT);
    }

    @Provides
    @CameraIntent Intent provideCameraIntent() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }
}
