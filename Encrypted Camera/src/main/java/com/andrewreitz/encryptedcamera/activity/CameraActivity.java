package com.andrewreitz.encryptedcamera.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.andrewreitz.encryptedcamera.BuildConfig;
import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.exception.SDCardException;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.google.common.net.MediaType;

import javax.inject.Inject;
import javax.inject.Named;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class CameraActivity extends BaseActivity implements ErrorDialog.ErrorDialogCallback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Inject
    @Named("camera-intent")
    Intent cameraIntent;

    @Inject
    ExternalStorageManager externalStorageManager;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            fileUri = externalStorageManager.getOutputMediaFileUri(MediaType.JPEG); // create a file to save the image
        } catch (SDCardException e) {
            Timber.e(e, "Error writing to sdcard");
            ErrorDialog errorDialog = ErrorDialog.newInstance(
                    getString(R.string.error_sdcard_title),
                    getString(R.string.error_sdcard_message)
            );
            errorDialog.setCallback(this);
            errorDialog.show(getFragmentManager(), "dialog_sdcard_error");
            return;
        }

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            return;
        }

        if (resultCode == RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            Timber.d("onActivityResult: RESULT_OK");
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture close the app
            Timber.d("onActivityResult: RESULT_CANCELED");
        } else {
            Timber.e("CameraActivity onActivityResult, unknown result received");
            if (BuildConfig.DEBUG) {
                throw new RuntimeException("CameraActivity onActivityResult, unknown result received");
            } else {
                ErrorDialog.newInstance(
                        getString(R.string.error),
                        getString(R.string.error_no_image_recieved)
                );
            }
        }
    }

    @Override
    public void onErrorDialogDismissed() {
        // Can't continue on close app
        finish();
    }
}
