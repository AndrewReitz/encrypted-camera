package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.activity.CameraActivity;
import com.andrewreitz.encryptedcamera.activity.SettingsActivity;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForActivity;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.UnlockNotification;
import com.andrewreitz.encryptedcamera.fragment.SettingsHomeFragment;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
@SuppressWarnings("UnusedDeclaration")
@Module(
        injects = {
                CameraActivity.class,
                SettingsHomeFragment.class
        },
        addsTo = AndroidModule.class,
        library = true
)
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = checkNotNull(activity);
    }

    /**
     * Allow the activity context to be injected but require that it be annotated with
     * {@link ForActivity @ForActivity} to explicitly differentiate it from application context.
     */
    @Provides
    @Singleton
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }

    @Provides
    @Singleton
    @UnlockNotification
    Notification provideUnlockNotification() {
        Notification notification = new NotificationCompat.Builder(activity)
                .setContentTitle(activity.getString(R.string.app_name))
                .setContentText(activity.getString(R.string.images_unencryped_message))
                .setContentIntent(
                        PendingIntent.getActivity(
                                activity,
                                0,
                                new Intent(
                                        activity,
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
}
