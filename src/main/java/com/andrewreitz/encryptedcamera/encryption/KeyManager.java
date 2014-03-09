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

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

public interface KeyManager {
    void saveKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException;

    void saveKey(String alias, SecretKey key) throws KeyStoreException;

    void saveKey(String alias, SecretKey key, String password) throws KeyStoreException;

    SecretKey getKey(String alias) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException;

    SecretKey getKey(String alias, String password) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException;

    SecretKey generateKeyWithPassword(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException;

    SecretKey generateKeyNoPassword() throws NoSuchAlgorithmException;
}
