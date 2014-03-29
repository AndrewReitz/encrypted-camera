package com.andrewreitz.encryptedcamera.image;

import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.TAG_ORIENTATION;

public final class ImageRotation {
    private ImageRotation() {
        // No Instances
    }

    /**
     * Checks if an image needs to be rotated. If there was an error 0 will be returned
     *
     * @param image image to check if rotation is needed
     * @return rotation value if one, otherwise 0
     * @throws IOException if the image can't be read (Like it's not an image)
     */
    public static int getRotation(File image) throws IOException {
        int rotation = 0;
        ExifInterface exif = new ExifInterface(image.getAbsolutePath());
        int orientation = exif.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
        }

        return rotation;
    }
}
