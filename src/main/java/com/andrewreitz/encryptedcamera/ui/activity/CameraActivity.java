/*
 *
 *  * Copyright (C) 2014 Andrew Reitz
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.andrewreitz.encryptedcamera.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.di.annotation.CameraIntent;
import com.andrewreitz.encryptedcamera.exception.SDCardException;
import com.andrewreitz.encryptedcamera.externalstoreage.ExternalStorageManager;
import com.andrewreitz.encryptedcamera.service.EncryptionIntentService;
import com.andrewreitz.encryptedcamera.sharedpreference.EncryptedCameraPreferenceManager;
import com.andrewreitz.encryptedcamera.ui.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.ui.fragment.FirstRunActivity;
import com.google.common.net.MediaType;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

public class CameraActivity extends BaseActivity implements ErrorDialog.ErrorDialogCallback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1234554321;

    @Inject @CameraIntent Intent cameraIntent;
    @Inject ExternalStorageManager externalStorageManager;
    @Inject EncryptedCameraPreferenceManager preferenceManager;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        openCameraWithIntent();

        if (!preferenceManager.hasSeenFirstRunFragment()) {
            FirstRunActivity.navigateTo(this);
        }
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
            encryptAndSaveImage(fileUri.getPath());
        }
        openCameraWithIntent();
    }

    @Override
    public void onErrorDialogDismissed() {
        // Can't continue on close app
        finish();
    }

    private void encryptAndSaveImage(final String unencryptedFilePath) {
        EncryptionIntentService.startEncryptAction(this, unencryptedFilePath);
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
