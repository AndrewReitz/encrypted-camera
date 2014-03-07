package com.andrewreitz.encryptedcamera.di.module;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.andrewreitz.encryptedcamera.di.annotation.ForActivity;
import com.andrewreitz.encryptedcamera.ui.activity.CameraActivity;
import com.andrewreitz.encryptedcamera.ui.activity.SettingsActivity;
import com.andrewreitz.encryptedcamera.ui.fragment.SettingsHomeFragment;

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
                SettingsHomeFragment.class,
                SettingsActivity.class
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
    FragmentManager provideFragmentManager() {
        return activity.getFragmentManager();
    }

    @Provides
    @Singleton
    GalleryAdapter provideGalleryAdapter() {
        return new GalleryAdapter
    }
}
