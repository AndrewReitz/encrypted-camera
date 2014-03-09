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

import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrew
 */
public class EncryptionProviderImpl implements EncryptionProvider {

    private final IvParameterSpec iv;
    private SecretKey secretKey;
    private final Cipher cipher;

    public EncryptionProviderImpl(Cipher cipher, SecretKey secretKey, byte[] iv) {
        checkArgument(iv.length == 16);
        this.cipher = checkNotNull(cipher);
        this.secretKey = checkNotNull(secretKey);
        this.iv = new IvParameterSpec(iv);
    }

    @Override public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String encrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        final byte[] input = !TextUtils.isEmpty(value) ? value.getBytes() : new byte[0];
        return Base64.encodeToString(
                this.encrypt(input),
                Base64.DEFAULT
        );
    }

    @Override
    public byte[] encrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        final byte[] input = value != null ? value : new byte[0];
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(input);
    }

    /**
     * Encrypts the in file and places it to the out file
     *
     * @param in  input file
     * @param out output file
     * @throws IOException
     * @throws InvalidKeyException
     */
    @Override
    public void encrypt(File in, File out) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        fileStreamEncryptDecrypt(in, out, Cipher.ENCRYPT_MODE);
    }

    @Override
    public void decrypt(File in, File out) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        fileStreamEncryptDecrypt(in, out, Cipher.DECRYPT_MODE);
    }

    private void fileStreamEncryptDecrypt(File in, File out, int optmode) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        checkNotNull(in);
        checkNotNull(out);

        cipher.init(optmode, secretKey, iv);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), cipher);

        copy(is, os);

        os.close();
    }

    @Override
    public String decrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        checkNotNull(value);

        final byte[] bytes = Base64.decode(value, Base64.DEFAULT);
        return new String(this.decrypt(bytes));
    }

    @Override
    public byte[] decrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        checkNotNull(value);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return cipher.doFinal(value);
    }

    /**
     * Copies the input stream to the output stream
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }
}
