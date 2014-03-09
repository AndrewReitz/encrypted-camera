/*
 * Copyright (C) 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andrewreitz.encryptedcamera.di.module;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.andrewreitz.encryptedcamera.di.annotation.ForActivity;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.ui.activity.CameraActivity;
import com.andrewreitz.encryptedcamera.ui.activity.GalleryActivity;
import com.andrewreitz.encryptedcamera.ui.activity.SettingsActivity;
import com.andrewreitz.encryptedcamera.ui.adapter.GalleryAdapter;
import com.andrewreitz.encryptedcamera.ui.fragment.SettingsHomeFragment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("UnusedDeclaration")
@Module(
        injects = {
                CameraActivity.class,
                SettingsHomeFragment.class,
                SettingsActivity.class,
                GalleryActivity.class
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
    @ForActivity Context provideActivityContext() {
        return activity;
    }

    @Provides
    @Singleton FragmentManager provideFragmentManager() {
        return activity.getFragmentManager();
    }

    @Provides
    @Singleton GalleryAdapter provideGalleryAdapter(LruCache<String, Bitmap> cache, ExternalStorageManager externalStorageManager) {
        // Wouldn't be null since it would have crashed earlier
        //noinspection ConstantConditions
        List<File> files = Arrays.asList(externalStorageManager.getAppExternalDirectory().listFiles());
        return new GalleryAdapter(activity, files, cache);
    }
}
