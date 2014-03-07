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
