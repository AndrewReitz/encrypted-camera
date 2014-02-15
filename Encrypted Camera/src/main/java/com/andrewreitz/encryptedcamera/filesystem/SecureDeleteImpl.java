package com.andrewreitz.encryptedcamera.filesystem;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
public class SecureDeleteImpl implements SecureDelete {

    private final SecureRandom secureRandom;

    public SecureDeleteImpl(@NotNull SecureRandom secureRandom) {
        this.secureRandom = checkNotNull(secureRandom);
    }

    @Override public void secureDelete(@NotNull File file) throws IOException {
        if (file.exists()) {
            long length = file.length();
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            raf.seek(0);
            raf.getFilePointer();
            byte[] data = new byte[64];
            int pos = 0;
            while (pos < length) {
                secureRandom.nextBytes(data);
                raf.write(data);
                pos += data.length;
            }
            raf.close();
            file.delete();
        }
    }
}
