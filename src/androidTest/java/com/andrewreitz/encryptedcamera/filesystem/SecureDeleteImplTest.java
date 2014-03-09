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

package com.andrewreitz.encryptedcamera.filesystem;

import android.test.AndroidTestCase;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.SecureRandom;

import static org.fest.assertions.api.Assertions.assertThat;

public class SecureDeleteImplTest extends AndroidTestCase {

    private SecureDelete secureDelete;

    @Override protected void setUp() throws Exception {
        super.setUp();

        this.secureDelete = new SecureDeleteImpl(new SecureRandom());
    }

    public void testShouldDeleteFile() throws Exception {
        // Arrange
        String inFileName = "test.txt";
        String textFileContent = "Test testing 1, 2, 3";
        //noinspection ConstantConditions
        File in = getContext().getFileStreamPath(inFileName);
        FileWriter outFile = new FileWriter(
                in
        );
        PrintWriter outWriter = new PrintWriter(outFile);
        outWriter.write(textFileContent);
        outWriter.close();

        // Act
        boolean deleted = secureDelete.secureDelete(in);

        // Assert
        assertThat(deleted).isTrue();
        assertThat(in.exists()).isFalse();
    }
}
