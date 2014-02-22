package com.andrewreitz.encryptedcamera.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.CameraIntent;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.InternalDecryptedDirectory;
import com.andrewreitz.encryptedcamera.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.exception.SDCardException;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.filesystem.SecureDelete;
import com.andrewreitz.encryptedcamera.service.EncryptionIntentService;
import com.andrewreitz.encryptedcamera.sharedpreference.EncryptedCameraPreferenceManager;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

public class CameraActivity extends BaseActivity implements ErrorDialog.ErrorDialogCallback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1234554321;

    @Inject @CameraIntent Intent cameraIntent;
    @Inject ExternalStorageManager externalStorageManager;
    @Inject EncryptedCameraPreferenceManager preferenceManager;
    @Inject SecureDelete secureDelete;
    @Inject @InternalDecryptedDirectory File internalDecryptedDirectory;

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
                try {
                    handleResultOk();
                } catch (IOException e) {
                    Timber.e(e, "Error handling response from camera");
                    showSdCardError();
                }
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

    private void handleResultOk() throws IOException {
        if (!preferenceManager.isDecrypted()) {
            File unencrypted = new File(fileUri.getPath());
            encryptAndSaveImage(unencrypted);
        }
    }

    @Override
    public void onErrorDialogDismissed() {
        // Can't continue on close app
        finish();
    }

    private void encryptAndSaveImage(File unencryptedFile) {
        EncryptionIntentService.startEncryptAction(this, unencryptedFile);
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
            showSdCardError();
            return;
        }

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void showSdCardError() {
        ErrorDialog errorDialog = ErrorDialog.newInstance(
                getString(R.string.error_sdcard_title),
                getString(R.string.error_sdcard_message)
        );
        errorDialog.setCallback(this);
        errorDialog.show(getFragmentManager(), "dialog_sdcard_error");
    }
}
