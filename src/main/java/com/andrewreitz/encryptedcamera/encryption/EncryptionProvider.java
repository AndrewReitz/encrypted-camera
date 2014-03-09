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

package com.andrewreitz.encryptedcamera.encryption;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

/**
 * @author Andrew
 */
public interface EncryptionProvider {
    void encrypt(File in, File out) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException;
    void decrypt(File in, File out) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException;
    String encrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException;
    String decrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException;
    byte[] encrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException;
    byte[] decrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException;
    void setSecretKey(SecretKey secretKey);
}
