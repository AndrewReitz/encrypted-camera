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

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.di.annotation.ForApplication;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProviderImpl;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.encryption.KeyManagerImpl;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
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
@SuppressWarnings("UnusedDeclaration")
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
    Cipher provideCipher() {
        try {
            return Cipher.getInstance(EncryptedCameraApp.CIPHER_TRANSFORMATION);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Singleton
    EncryptionProvider provideEncryptionProvider(Cipher cipher, KeyManager keyManager) {
        try {
            return new EncryptionProviderImpl(
                    cipher,
                    keyManager.getKey(EncryptedCameraApp.KEY_STORE_ALIAS),
                    new byte[] { // TODO MOVE TO SHARED PREFS
                            0x4,0xA,0xF,0xF,0x4,0x5,0x9,0x5,
                            0x0,0x2,0x0,0x7,0x9,0x3,0xd,0x2
                    }
            );
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            Timber.w(e, "Could not create EncryptionProvider");
            throw new RuntimeException(e);
        }
    }
}
