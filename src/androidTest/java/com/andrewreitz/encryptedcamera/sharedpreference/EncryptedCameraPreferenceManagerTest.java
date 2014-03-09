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

package com.andrewreitz.encryptedcamera.sharedpreference;

import android.content.Context;
import android.test.AndroidTestCase;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.di.module.AndroidModule;

import javax.inject.Inject;

import dagger.Module;
import dagger.ObjectGraph;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author areitz
 */
public class EncryptedCameraPreferenceManagerTest extends AndroidTestCase {

    @Inject SharedPreferenceService sharedPreferenceService;

    private EncryptedCameraPreferenceManager preferenceManager;

    @Override public void setUp() throws Exception {
        super.setUp();

        Context context = getContext();
        //noinspection ConstantConditions
        ObjectGraph.create(
                TestModule.class,
                new AndroidModule((EncryptedCameraApp) context.getApplicationContext())
        ).inject(this);

        preferenceManager = new EncryptedCameraPreferenceManager(
                context,
                sharedPreferenceService
        );
    }

    public void testShouldSaveAndGetSalt() {
        // Arrange
        String salt = "mySalt";

        // Act
        preferenceManager.setSalt(salt.getBytes());
        byte[] result = preferenceManager.getSalt();

        // Assert
        assertThat(result).isEqualTo(salt.getBytes());
    }

    @Module(
            includes = AndroidModule.class,
            injects = EncryptedCameraPreferenceManagerTest.class,
            overrides = true
    )
    static class TestModule {
    }
}
