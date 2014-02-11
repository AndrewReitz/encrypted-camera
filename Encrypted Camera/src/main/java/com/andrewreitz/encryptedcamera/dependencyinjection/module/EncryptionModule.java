package com.andrewreitz.encryptedcamera.dependencyinjection.module;

import android.content.Context;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProviderImp;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.encryption.KeyManagerImpl;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

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

    @Provides
    @Singleton
    EncryptionProvider provideEncryptionProvider(Cipher cipher, KeyManager keyManager) {
        try {
            return new EncryptionProviderImp(
                    cipher,
                    keyManager.getKey(EncryptedCameraApp.KEY_STORE_ALIAS)
            );
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            Timber.w(e, "Could not create EncryptionProvider");
            throw new RuntimeException(e);
        }
    }
}
