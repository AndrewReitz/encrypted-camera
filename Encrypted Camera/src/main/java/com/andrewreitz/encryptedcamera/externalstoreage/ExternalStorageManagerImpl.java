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

/**
 * @author areitz
 */
public class ExternalStorageManagerImpl implements ExternalStorageManager {

    private static final String FILE_NAME_FORMAT = "%s%s%s_%s.%s";
    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd_HHmmss";

    // TODO Probably add constructors to override these
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

    /**
     * Create / Check that the application directory is available.  This is done based off your application's name
     * saved in String.app_name
     *
     * @return null if unable to create folder otherwise returns the directory
     */
    @Override
    public File getAppExternalDirectory() {
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                context.getString(R.string.app_name).replace(" ", "") // remove any spaces in case the system doesn't like
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
     * Checks if you can read and write on the SDCard
     *
     * @return true if it's in a r/w state
     */
    @Override
    public boolean checkSdCardIsInReadAndWriteState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private File getMediaFile(MediaType type, File mediaStorageDir) {
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
