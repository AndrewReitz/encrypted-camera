package com.andrewreitz.encryptedcamera.di.module;

import dagger.Module;

/**
 * Used for compile time validation
 *
 * @author areitz
 */
@SuppressWarnings("UnusedDeclaration")
@Module(
        includes = {
                AndroidModule.class,
                ActivityModule.class
        }
)
public class EncryptedCameraAppModule {
}
