package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.sharedpreference.DefaultSharedPreferenceService;
import com.andrewreitz.encryptedcamera.sharedpreference.SharedPreferenceService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
@Module(library = true)
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
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreference() {
        return PreferenceManager.getDefaultSharedPreferences(this.application);
    }

    @Provides
    @Singleton
    SharedPreferenceService provideSharedPreferenceService(SharedPreferences sharedPreferences) {
        return new DefaultSharedPreferenceService(sharedPreferences);
    }
}
