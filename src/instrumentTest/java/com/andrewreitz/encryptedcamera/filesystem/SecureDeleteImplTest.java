package com.andrewreitz.encryptedcamera.filesystem;

import android.test.AndroidTestCase;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.SecureRandom;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author areitz
 */
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
