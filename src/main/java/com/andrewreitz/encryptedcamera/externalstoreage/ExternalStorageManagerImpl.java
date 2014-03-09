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

package com.andrewreitz.encryptedcamera.externalstoreage;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.exception.SDCardException;
import com.google.common.net.MediaType;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExternalStorageManagerImpl implements ExternalStorageManager {

    private static final String FILE_NAME_FORMAT = "%s%s%s_%s.%s";
    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd_HHmmss";

    private static final String IMAGE_FILENAME_PREFIX = "IMG";
    private static final String VIDEO_FILENAME_PREFIX = "VID";

    private final Context context;
    private final DateFormat dateFormat;

    public ExternalStorageManagerImpl(Context context) {
        this(context, null);
    }

    public ExternalStorageManagerImpl(Context context, DateFormat dateFormat) {
        this.context = checkNotNull(context).getApplicationContext();
        this.dateFormat = dateFormat == null ? new SimpleDateFormat(DEFAULT_DATE_FORMAT) : dateFormat;
    }

    /**
     * Create a file Uri for saving an image or video
     */
    @Override
    public Uri getOutputMediaFileUri(MediaType type) throws SDCardException {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    @Override
    public File getOutputMediaFile(MediaType type) throws SDCardException {
        checkNotNull(type);

        // Check that the SDCard is mounted if not throw exception to be handled at UI
        if (!checkSdCardIsInReadAndWriteState()) {
            throw new SDCardException(
                    String.format(
                            "SDCard is not in a valid state, currently in %s", Environment.getExternalStorageState()
                    )
            );
        }

        File mediaStorageDir = getAppExternalDirectory();
        if (mediaStorageDir == null) {
            throw new SDCardException(
                    "Unable to create directory on sdcard"
            );
        }

        // Create a media file name
        return getMediaFile(type, mediaStorageDir);
    }

    @Override public Uri getHiddenOutputMediaFileUri(MediaType type) throws SDCardException {
        return Uri.fromFile(getHiddenOutputMediaFile(type));
    }

    @Override public File getHiddenOutputMediaFile(MediaType type) throws SDCardException {
        checkNotNull(type);

        // Check that the SDCard is mounted if not throw exception to be handled at UI
        if (!checkSdCardIsInReadAndWriteState()) {
            throw new SDCardException(
                    String.format(
                            "SDCard is not in a valid state, currently in %s", Environment.getExternalStorageState()
                    )
            );
        }

        // TODO generalize this more if pulling into a library
        File mediaStorageDir = getHiddenAppExternalDirectory();
        if (mediaStorageDir == null) {
            throw new SDCardException(
                    "Unable to create directory on sdcard"
            );
        }

        // Create a media file name
        return getMediaFile(type, mediaStorageDir);
    }

    /**
     * Create / Check that the application directory is available.  This is done based off your application's name
     * saved in String.app_name @see getApplicationNameNoSpaces
     *
     * @return null if unable to create folder otherwise returns the directory
     */
    @Override
    public File getAppExternalDirectory() {
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                getApplicationNameNoSpaces()
        );

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    /**
     * Create / Check that the application directory is available.  This is done based off your application's name
     * saved in String.app_name @see getApplicationNameNoSpaces with a . in front of it so it's hidden
     * TODO If ever moving this out to a library should probably generalize this call
     *
     * @return null if unable to create folder otherwise returns the directory
     */
    @Override
    public File getHiddenAppExternalDirectory() {
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                String.format(".%s", getApplicationNameNoSpaces())
        );

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    /**
     * Gets the applications directory based on the app's name in strings "app_name".  If you want to
     * need a different directory override this
     *
     * @return Applications name stripping out all spaces
     */
    protected String getApplicationNameNoSpaces() {
        // remove any spaces in case the system doesn't like
        return context.getString(R.string.app_name).replace(" ", "");
    }

    /**
     * Checks if you can read and write on the SDCard
     *
     * @return true if it's in a r/w state
     */
    @Override
    public boolean checkSdCardIsInReadAndWriteState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    protected File getMediaFile(MediaType type, File mediaStorageDir) {
        String timeStamp = dateFormat.format(new Date());
        File mediaFile;
        if (type.is(MediaType.ANY_IMAGE_TYPE)) {
            mediaFile = new File(
                    getFileName(type, mediaStorageDir, IMAGE_FILENAME_PREFIX, timeStamp)
            );
        } else if (type.is(MediaType.ANY_VIDEO_TYPE)) {
            mediaFile = new File(
                    getFileName(type, mediaStorageDir, VIDEO_FILENAME_PREFIX, timeStamp)
            );
        } else if (type.is(MediaType.ANY_TYPE)) {
            mediaFile = new File(
                    mediaStorageDir.getPath()
            );
        } else {
            throw new IllegalArgumentException(
                    String.format("Unknown File Type %s", type)
            );
        }
        return mediaFile;
    }

    private String getFileName(MediaType type, File mediaStorageDir, String mediaPrefix, String timeStamp) {
        return String.format(
                FILE_NAME_FORMAT,
                mediaStorageDir.getPath(),
                File.separator,
                mediaPrefix,
                timeStamp,
                type.subtype()
        );
    }
}
