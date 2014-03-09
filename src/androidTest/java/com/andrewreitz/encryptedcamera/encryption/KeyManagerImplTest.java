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

import android.test.AndroidTestCase;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("ConstantConditions")
public class KeyManagerImplTest extends AndroidTestCase {

    private String KEY_STORE_NAME = "test.keystore";
    private KeyManager keyManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        keyManager = new KeyManagerImpl(KEY_STORE_NAME, getContext());
    }

    public void testSaveKeyStore() throws Exception {
        keyManager.saveKeyStore();
        File file = getContext().getFileStreamPath(KEY_STORE_NAME);
        assertThat(file).exists();
    }

    public void testShouldGenerateKeySaveKeyAndGetKey() throws Exception {
        // Arrange
        String alias = "test";

        // Act
        SecretKey secretKey = keyManager.generateKeyNoPassword();
        keyManager.saveKey(alias, secretKey);
        SecretKey key = keyManager.getKey(alias);

        // Assert
        assertThat(key.getAlgorithm()).isEqualTo(secretKey.getAlgorithm());
        assertThat(key.getEncoded()).isEqualTo(secretKey.getEncoded());
        assertThat(key.getFormat()).isEqualTo(secretKey.getFormat());
    }

    public void testShouldGenerateKeySaveKeyWithAPasswordAndGetKeyWithPassword() throws Exception {
        // Arrange
        String password = "immasupersecretpassword";
        String alias = "test";

        // Act
        SecretKey secretKey = keyManager.generateKeyNoPassword();
        keyManager.saveKey(alias, secretKey, password);
        SecretKey key = keyManager.getKey(alias, password);

        // Assert
        assertThat(key.getAlgorithm()).isEqualTo(secretKey.getAlgorithm());
        assertThat(key.getEncoded()).isEqualTo(secretKey.getEncoded());
        assertThat(key.getFormat()).isEqualTo(secretKey.getFormat());
    }

    public void testShouldCreateSameKeysForEncryptionAndDecryption() throws Exception {
        // Arrange
        String password = "immasupersecretpassword";
        String salt = "immasalt";

        // Act
        SecretKey secretKey1 = keyManager.generateKeyWithPassword(password.toCharArray(), salt.getBytes());
        SecretKey secretKey2 = keyManager.generateKeyWithPassword(password.toCharArray(), salt.getBytes());

        // Assert
        assertThat(secretKey1).isEqualsToByComparingFields(secretKey2);
    }
}
