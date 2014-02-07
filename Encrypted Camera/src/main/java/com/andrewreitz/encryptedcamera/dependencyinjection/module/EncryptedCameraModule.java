package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import dagger.Module;

/**
 * @author areitz
 */
@Module(
        includes = {
                AndroidModule.class,
                ActivityModule.class
        }
)
public class EncryptedCameraModule {
}
