package com.andrewreitz.encryptedcamera.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * @author areitz
 */
public interface SecureDelete {

    /**
     * Writes random bytes to the file then deletes it to make it unrecoverable.
     *
     * @param file the file to delete
     * @return true if the file was deleted or if the file never existed
     * @throws IOException
     */
    boolean secureDelete(File file) throws IOException;
}
