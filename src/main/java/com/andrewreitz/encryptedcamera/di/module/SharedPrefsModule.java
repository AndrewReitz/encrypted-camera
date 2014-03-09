/*
 *
 *  * Copyright (C) 2014 Andrew Reitz
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.andrewreitz.encryptedcamera.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.andrewreitz.encryptedcamera.di.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.sharedpreference.DefaultSharedPreferenceService;
import com.andrewreitz.encryptedcamera.sharedpreference.SharedPreferenceService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author areitz
 */
@SuppressWarnings("UnusedDeclaration")
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
