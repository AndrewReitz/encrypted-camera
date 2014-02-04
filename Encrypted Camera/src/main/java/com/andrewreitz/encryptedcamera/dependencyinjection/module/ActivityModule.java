package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;

import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForActivity;
import com.andrewreitz.encryptedcamera.fragment.SettingsHomeFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
@Module(
        injects = {
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
    NotificationManager provideNotificationManager() {
        return  (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
