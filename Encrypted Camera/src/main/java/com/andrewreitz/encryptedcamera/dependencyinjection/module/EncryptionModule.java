package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.content.Context;

import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.encryption.KeyManagerImpl;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

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
public class EncryptionModule {

    @Provides
    @Singleton
    KeyManager provideKeyManager(@ForApplication Context context) {
        try {
            return new KeyManagerImpl(context);
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
