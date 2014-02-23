package com.andrewreitz.encryptedcamera.externalstoreage;

import android.net.Uri;

import com.andrewreitz.encryptedcamera.exception.SDCardException;
import com.google.common.net.MediaType;

import java.io.File;

/**
 * @author areitz
 */
public interface ExternalStorageManager {
    Uri getOutputMediaFileUri(MediaType type) throws SDCardException;

    File getOutputMediaFile(MediaType type) throws SDCardException;

    File getAppExternalDirectory();

    boolean checkSdCardIsInReadAndWriteState();
}
