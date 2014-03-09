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

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.di.module.AndroidModule;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.Cipher;
import javax.inject.Inject;

import dagger.Module;
import dagger.ObjectGraph;

import static org.fest.assertions.api.Assertions.assertThat;

public class EncryptionProviderImplTest extends AndroidTestCase {

    @Inject Cipher cipher;
    @Inject KeyManager keyManager;

    @SuppressWarnings("ConstantConditions")
    @Override protected void setUp() throws Exception {
        super.setUp();

        ObjectGraph.create(
                TestModule.class,
                new AndroidModule(
                        (EncryptedCameraApp) getContext().getApplicationContext()
                )
        ).inject(this);
    }

    public void testEncryptAndDecryptStringNoPassword() throws Exception {
        // Arrange
        EncryptionProvider encryptionProvider = getEncryptionProviderNoPassword();
        String expected = "testString";

        // Act
        String encrypted = encryptionProvider.encrypt(expected);
        String actual = encryptionProvider.decrypt(encrypted);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    public void testEncryptAndDecryptString() throws Exception {
        // Arrange
        EncryptionProvider encryptionProvider = getEncryptionProviderWithPassword();
        String expected = "testString";

        // Act
        String encrypted = encryptionProvider.encrypt(expected);
        String actual = encryptionProvider.decrypt(encrypted);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @SuppressWarnings("ConstantConditions")
    public void testEncyptAndDecryptFileNoPassword() throws Exception {
        // Arrange
        EncryptionProvider encryptionProvider = getEncryptionProviderNoPassword();
        String inFileName = "test.txt";
        String outFileName = "out";
        String actualFileName = "actual.txt";
        String textFileContent = "Test testing 1, 2, 3";
        File in = getContext().getFileStreamPath(inFileName);
        File out = getContext().getFileStreamPath(outFileName);
        File actual = getContext().getFileStreamPath(actualFileName);
        FileWriter outFile = new FileWriter(
                in
        );
        PrintWriter outWriter = new PrintWriter(outFile);
        outWriter.write(textFileContent);
        outWriter.close();

        // Act
        encryptionProvider.encrypt(in, out);
        encryptionProvider.decrypt(out, actual);

        List<String> strings = Files.readLines(actual, Charset.defaultCharset());

        // Assert
        assertThat(strings.get(0)).isEqualTo(textFileContent);
    }


    @SuppressWarnings("ConstantConditions")
    public void testEncyptAndDecryptFileWithPassword() throws Exception {
        // Arrange
        EncryptionProvider encryptionProvider = getEncryptionProviderWithPassword();
        String inFileName = "test.txt";
        String outFileName = "out";
        String actualFileName = "actual.txt";
        String textFileContent = "Test testing 1, 2, 3";
        File in = getContext().getFileStreamPath(inFileName);
        File out = getContext().getFileStreamPath(outFileName);
        File acutal = getContext().getFileStreamPath(actualFileName);
        FileWriter outFile = new FileWriter(
                in
        );
        PrintWriter outWriter = new PrintWriter(outFile);
        outWriter.write(textFileContent);
        outWriter.close();

        // Act
        encryptionProvider.encrypt(in, out);
        encryptionProvider.decrypt(out, acutal);

        List<String> strings = Files.readLines(acutal, Charset.defaultCharset());

        // Assert
        assertThat(strings.get(0)).isEqualTo(textFileContent);
    }

    private EncryptionProvider getEncryptionProviderNoPassword() throws NoSuchAlgorithmException {
        return new EncryptionProviderImpl(
                cipher,
                keyManager.generateKeyNoPassword(),
                new byte[] {
                        0x4,0xA,0xF,0xF,0x4,0x5,0x9,0x5,
                        0x0,0x2,0x0,0x7,0x9,0x3,0xd,0x2
                }
        );
    }

    private EncryptionProvider getEncryptionProviderWithPassword() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return new EncryptionProviderImpl(
                cipher,
                keyManager.generateKeyWithPassword("testpassword".toCharArray(), "ImmaSalt".getBytes()),
                new byte[] {
                        0x4,0xA,0xF,0xF,0x4,0x5,0x9,0x5,
                        0x0,0x2,0x0,0x7,0x9,0x3,0xd,0x2
                }
        );
    }

    @Module(
            includes = AndroidModule.class,
            injects = EncryptionProviderImplTest.class,
            overrides = true
    )
    static class TestModule {
    }
}
