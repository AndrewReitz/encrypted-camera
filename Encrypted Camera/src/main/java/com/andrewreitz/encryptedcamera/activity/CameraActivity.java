package com.andrewreitz.encryptedcamera.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

public class CameraActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Inject
    @Named("camera-intent")
    Intent cameraIntent;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            Timber.d("onActivityResult: RESULT_OK");
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture close the app
            Timber.d("onActivityResult: RESULT_CANCELED");
            finish();
        } else {
            //            Logger.log(Logger.LogLevel.DEBUG, "onActivityResult: NO RESULT SHOWN");
            //            ErrorDialog errorDialog = ErrorDialog.newInstance("Error",
            //                    "Unknown Result Code " + resultCode);
            //            errorDialog.setCallback(this);
            //            errorDialog.show(getSupportFragmentManager(), "error_dialog");
        }
    }
}
