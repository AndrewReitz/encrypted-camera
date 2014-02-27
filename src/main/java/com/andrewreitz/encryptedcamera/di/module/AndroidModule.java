package com.andrewreitz.encryptedcamera.di.module;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.ui.activity.SettingsActivity;
import com.andrewreitz.encryptedcamera.di.annotation.CameraIntent;
import com.andrewreitz.encryptedcamera.di.annotation.EncryptionErrorNotification;
import com.andrewreitz.encryptedcamera.di.annotation.EncryptionNotification;
import com.andrewreitz.encryptedcamera.di.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.di.annotation.MediaFormat;
import com.andrewreitz.encryptedcamera.di.annotation.UnlockNotification;
import com.andrewreitz.encryptedcamera.service.EncryptionIntentService;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
        injects = {
                EncryptedCameraApp.class,
                EncryptionIntentService.class
        }
)
public class AndroidModule {

    private final EncryptedCameraApp application;

    public AndroidModule(EncryptedCameraApp application) {
        this.application = checkNotNull(application);
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link com.andrewreitz.encryptedcamera.di.annotation.ForApplication
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
    @EncryptionNotification Notification provideEncryptionNotification() {
        return new NotificationCompat.Builder(application)
                .setContentTitle("Encrypted Camera") // TODO ... you know what
                .setContentText("Encrypting your photos")
                .setSmallIcon(R.drawable.ic_unlocked) //TODO New Icon
                .build();
    }

    @Provides
    @Singleton
    @EncryptionErrorNotification Notification provideEncryptionErrorNotification() {
        Notification notification = new NotificationCompat.Builder(application)
                .setProgress(0, 0, true)
                .setContentTitle(application.getString(R.string.error_encrypting))
                .setContentText(application.getString(R.string.error_encrypting_photo))
                .setSmallIcon(R.drawable.ic_unlocked) //TODO New Icon
                .build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        return notification;
    }

    @Provides
    @Singleton
    @UnlockNotification Notification provideUnlockNotification() {
        Notification notification = new NotificationCompat.Builder(application)
                .setContentTitle(application.getString(R.string.app_name))
                .setContentText(application.getString(R.string.images_unencryped_message))
                .setContentIntent(
                        PendingIntent.getActivity(
                                application,
                                0,
                                new Intent(
                                        application,
                                        SettingsActivity.class
                                ),
                                0
                        )
                )
                .setSmallIcon(R.drawable.ic_unlocked)
                .build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        return notification;
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
