package com.andrewreitz.encryptedcamera.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.CameraIntent;
import com.andrewreitz.encryptedcamera.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.exception.SDCardException;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.service.EncryptionIntentService;
import com.google.common.net.MediaType;

import java.io.File;

import javax.inject.Inject;

import timber.log.Timber;

public class CameraActivity extends BaseActivity implements ErrorDialog.ErrorDialogCallback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1234554321;

    @Inject @CameraIntent Intent cameraIntent;
    @Inject ExternalStorageManager externalStorageManager;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        openCameraWithIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            return;
        }

        switch (resultCode) {
            case RESULT_OK:
                encryptAndSaveImage();
                break;
            case RESULT_CANCELED:
                // User cancelled taking a photo close the app
                finish();
                break;
            default:
                Timber.e(
                        "CameraActivity onActivityResult, unknown result received: requestCode %s, resultCode %s",
                        requestCode,
                        resultCode
                );
                ErrorDialog errorDialog = ErrorDialog.newInstance(
                        getString(R.string.error),
                        getString(R.string.error_no_image_recieved)
                );
                errorDialog.show(getFragmentManager(), "dialog_unknown_camera_result");
                break;
        }
    }

    @Override
    public void onErrorDialogDismissed() {
        // Can't continue on close app
        finish();
    }

    private void encryptAndSaveImage() {
        // Image captured and saved to fileUri specified in the Intent
        // We need to encrypt it and save to EncryptedFolder
        File unencryptedImage = new File(fileUri.getPath());
        EncryptionIntentService.queue(this, unencryptedImage);
        openCameraWithIntent();
    }

    private void openCameraWithIntent() {
        //noinspection ConstantConditions
        if (cameraIntent.resolveActivity(getPackageManager()) == null) {
            Timber.i("No camera application found");
            ErrorDialog errorDialog = ErrorDialog.newInstance(
                    getString(R.string.error_no_camera_app),
                    getString(R.string.error_no_camera_app_found_message)
            );
            errorDialog.setCallback(this);
            errorDialog.show(getFragmentManager(), "dialog_no_camera_app_error");
            return;
        }

        try {
            // create a file to save the image
            fileUri = externalStorageManager.getOutputMediaFileUri(MediaType.JPEG);
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
}
