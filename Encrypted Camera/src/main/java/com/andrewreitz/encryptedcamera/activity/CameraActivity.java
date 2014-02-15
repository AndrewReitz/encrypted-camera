package com.andrewreitz.encryptedcamera.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.andrewreitz.encryptedcamera.BuildConfig;
import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.CameraIntent;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.EncryptedDirectory;
import com.andrewreitz.encryptedcamera.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;
import com.andrewreitz.encryptedcamera.exception.SDCardException;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.google.common.net.MediaType;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.inject.Inject;

import timber.log.Timber;

public class CameraActivity extends BaseActivity implements ErrorDialog.ErrorDialogCallback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 12345;

    @Inject @CameraIntent Intent cameraIntent;
    @Inject ExternalStorageManager externalStorageManager;
    @Inject EncryptionProvider encryptionProvider;
    @Inject @EncryptedDirectory File encryptedFileDirectory;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openCameraWithIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            return;
        }

        switch (resultCode) {
            case RESULT_OK:
                // Image captured and saved to fileUri specified in the Intent
                // We need to encrypt it and save to EncryptedFolder
                File unencryptedImage = new File(fileUri.getPath());
                // D/C about name since it's saved internally
                File encryptedFile = new File(encryptedFileDirectory, unencryptedImage.getName());
                try {
                    //noinspection ResultOfMethodCallIgnored
                    encryptedFile.createNewFile();
                    encryptionProvider.encrypt(unencryptedImage, encryptedFile);
                } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                    // TODO
                    throw new RuntimeException(e);
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
                if (BuildConfig.DEBUG) {
                    throw new RuntimeException("CameraActivity onActivityResult, unknown result received");
                } else {
                    ErrorDialog.newInstance(
                            getString(R.string.error),
                            getString(R.string.error_no_image_recieved)
                    );
                }
                break;
        }
    }

    @Override
    public void onErrorDialogDismissed() {
        // Can't continue on close app
        finish();
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
