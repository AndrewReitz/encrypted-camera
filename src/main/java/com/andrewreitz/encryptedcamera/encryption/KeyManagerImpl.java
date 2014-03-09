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

package com.andrewreitz.encryptedcamera.encryption;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.google.common.base.Preconditions.checkNotNull;

public class KeyManagerImpl implements KeyManager {
    private final Context context;
    private final KeyStore keyStore;
    private String keyStoreName = "app.keystore"; // default name

    public KeyManagerImpl(@NotNull Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        this.context = checkNotNull(context);
        keyStore = getKeyStore();
    }

    public KeyManagerImpl(@Nullable String keystoreName, @NotNull Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        this.context = checkNotNull(context);
        keyStore = getKeyStore();
        if (keystoreName != null) this.keyStoreName = keystoreName;
    }

    private KeyStore getKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        File file = context.getFileStreamPath(keyStoreName);
        if (file.exists()) {
            ks.load(context.openFileInput(keyStoreName), null);
        } else {
            ks.load(null, null);
        }
        return ks;
    }

    @Override
    public void saveKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        FileOutputStream fos = context.openFileOutput(keyStoreName, Context.MODE_PRIVATE);
        keyStore.store(fos, null);
        fos.close();
    }

    @Override
    public void saveKey(@NotNull String alias, @NotNull SecretKey key) throws KeyStoreException {
        this.saveKey(alias, key, null);
    }

    @Override
    public void saveKey(@NotNull String alias, @NotNull SecretKey key, @Nullable String password) throws KeyStoreException {
        keyStore.setKeyEntry(
                checkNotNull(alias),
                key,
                password == null ? null : password.toCharArray(),
                null
        );
    }

    @Override
    public SecretKey getKey(@NotNull String alias) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return this.getKey(alias, null);
    }

    @Override
    public SecretKey generateKeyWithPassword(char[] passphraseOrPin, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey temp = secretKeyFactory.generateSecret(keySpec);
        return new SecretKeySpec(temp.getEncoded(), "AES");
    }

    @Override
    public SecretKey generateKeyNoPassword() throws NoSuchAlgorithmException {
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        return keyGenerator.generateKey();
    }

    /**
     * Get key from a password protected keymanager
     */
    @Override
    public SecretKey getKey(@NotNull String alias, @Nullable String password) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (SecretKey) keyStore.getKey(alias, password == null ? null : password.toCharArray());
    }
}
