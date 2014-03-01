package com.andrewreitz.encryptedcamera.filesystem;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

import static com.google.common.base.Preconditions.checkNotNull;

public class SecureDeleteImpl implements SecureDelete {

    private final SecureRandom secureRandom;

    public SecureDeleteImpl(@NotNull SecureRandom secureRandom) {
        this.secureRandom = checkNotNull(secureRandom);
    }

    @Override public boolean secureDelete(@NotNull File file) throws IOException {
        if (file.exists()) {
            long length = file.length();
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            raf.seek(0);
            raf.getFilePointer();
            byte[] data = new byte[1024];
            int pos = 0;
            while (pos < length) {
                secureRandom.nextBytes(data);
                raf.write(data);
                pos += data.length;
            }
            raf.close();
            return file.delete();
        }

        return true; // didn't exist so we technically deleted...
    }
}
