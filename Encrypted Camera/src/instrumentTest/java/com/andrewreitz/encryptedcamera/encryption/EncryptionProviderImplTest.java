package com.andrewreitz.encryptedcamera.encryption;

import android.test.AndroidTestCase;

import com.andrewreitz.encryptedcamera.EncryptedCameraApp;
import com.andrewreitz.encryptedcamera.dependencyinjection.module.AndroidModule;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import javax.crypto.Cipher;
import javax.inject.Inject;

import dagger.Module;
import dagger.ObjectGraph;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Andrew
 */
public class EncryptionProviderImplTest extends AndroidTestCase {

    @Inject Cipher cipher;
    @Inject KeyManager keyManager;

    private EncryptionProviderImp encryptionProvider;

    @SuppressWarnings("ConstantConditions")
    @Override protected void setUp() throws Exception {
        super.setUp();

        ObjectGraph.create(
                TestModule.class,
                new AndroidModule(
                        (EncryptedCameraApp) getContext().getApplicationContext()
                )
        ).inject(this);

        encryptionProvider = new EncryptionProviderImp(
                cipher,
                keyManager.generateKeyNoPassword(),
                new byte[] {
                        0x4,0xA,0xF,0xF,0x4,0x5,0x9,0x5,
                        0x0,0x2,0x0,0x7,0x9,0x3,0xd,0x2
                }
        );
    }

    public void testEncryptAndDecryptString() throws Exception {
        // Arrange
        String expected = "testString";

        // Act
        String encrypted = encryptionProvider.encrypt(expected);
        String actual = encryptionProvider.decrypt(encrypted);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @SuppressWarnings("ConstantConditions")
    public void testEncyptAndDecryptFile() throws Exception {
        // Arrange
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

    @Module(
            includes = AndroidModule.class,
            injects = EncryptionProviderImplTest.class,
            overrides = true
    )
    static class TestModule {
    }
}
