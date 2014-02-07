package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.sharedpreference.DefaultSharedPreferenceService;
import com.andrewreitz.encryptedcamera.sharedpreference.SharedPreferenceService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author areitz
 */
@Module(
        complete = false,
        library = true
)
public class SharedPrefsModule {
    @Provides
    @Singleton
    SharedPreferences provideSharedPreference(@ForApplication Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    SharedPreferenceService provideSharedPreferenceService(SharedPreferences sharedPreferences) {
        return new DefaultSharedPreferenceService(sharedPreferences);
    }
}
