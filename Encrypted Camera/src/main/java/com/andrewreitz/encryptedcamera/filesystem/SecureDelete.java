package com.andrewreitz.encryptedcamera.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * @author areitz
 */
public interface SecureDelete {
    boolean secureDelete(File file) throws IOException;
}
