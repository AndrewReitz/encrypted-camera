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

import android.test.AndroidTestCase;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.di.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.di.module.AndroidModule;
import com.andrewreitz.encryptedcamera.dev.test.R;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.inject.Inject;

import dagger.Module;
import dagger.ObjectGraph;

/** Because I was having issues with how the decryption was working, so, I made a test */
@SuppressWarnings("ConstantConditions") public class FullEncryptionTest extends AndroidTestCase {

    private static final String ALIAS = "MyAlias";
    private static final String PASSWORD = "helloImapassword";
    private static final String SALT = "immasalt";

    @Inject KeyManager keyManager;
    @Inject @EncryptedDirectory File encrtypedDirectory;
    @Inject ExternalStorageManager externalStorageManager;
    @Inject Cipher cipher;

    @Override protected void setUp() throws Exception {
        super.setUp();

        ObjectGraph.create(
                TestModule.class,
                new AndroidModule((EncryptedCameraApp) getContext().getApplicationContext()))
                .inject(this);
    }

    public void testShouldEncryptFilesToInternalStorage() throws Exception {
        // Generate and save a keys
        SecretKey secretKey = keyManager.generateKeyWithPassword(PASSWORD.toCharArray(), SALT.getBytes());
        keyManager.saveKey(ALIAS, secretKey);
        keyManager.saveKeyStore();

        // Move Images to sdcard
        List<File> files = Lists.newArrayList();
        files.add(moveFileToSdCard(R.raw.img1));
        files.add(moveFileToSdCard(R.raw.img2));
        files.add(moveFileToSdCard(R.raw.img3));

        // Encrypt files
        EncryptionProvider encryptionProvider = new EncryptionProviderImpl(
                cipher,
                secretKey,
                new byte[]{
                        0x4, 0xA, 0xF, 0xF, 0x4, 0x5, 0x9, 0x5,
                        0x0, 0x2, 0x0, 0x7, 0x9, 0x3, 0xd, 0x2
                }
        );

        SecretKey keyAgain = keyManager.generateKeyWithPassword(PASSWORD.toCharArray(), SALT.getBytes());

        for (File file : files) {
            File out = new File(this.encrtypedDirectory, file.getName());
            encryptionProvider.encrypt(file, out);

            File outUnecrypted = new File(file.getParentFile(), file.getName());
            encryptionProvider.decrypt(out, outUnecrypted);

            File outUnecrypted2 = new File(file.getParentFile(), file.getName() + "2");
            encryptionProvider.setSecretKey(keyAgain);
            encryptionProvider.decrypt(out, outUnecrypted2);
        }
    }

    private File moveFileToSdCard(int resourceId) throws Exception {
        File file = externalStorageManager.getOutputMediaFile(MediaType.JPEG);
        InputStream inputStream = getContext().getResources().openRawResource(resourceId);
        OutputStream outputStream = new FileOutputStream(file);

        int read;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        outputStream.close();
        inputStream.close();

        return file;
    }

    @Module(
            includes = AndroidModule.class,
            injects = FullEncryptionTest.class,
            overrides = true
    )
    static class TestModule {
    }
}
