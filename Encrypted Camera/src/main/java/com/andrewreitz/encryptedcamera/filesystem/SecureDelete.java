package com.andrewreitz.encryptedcamera.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author areitz
 */
public interface SecureDelete {
    void secureDelete(File file) throws IOException;
}
