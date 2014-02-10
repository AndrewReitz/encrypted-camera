package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import dagger.Module;

/**
 * Used for compile time validation
 *
 * @author areitz
 */
@Module(
        includes = {
                AndroidModule.class,
                ActivityModule.class
        }
)
public class EncryptedCameraAppModule {
}
