package co.nodeath.encryptedcamera.presentation.activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.nerdery.bootstrap.log.Logger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.IOException;

import co.nodeath.encryptedcamera.business.CameraManager;
import co.nodeath.encryptedcamera.presentation.controller.CameraController;
import co.nodeath.encryptedcamera.presentation.dialog.ErrorDialog;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Main activity of the application
 *
 * @author areitz
 */
public class CameraActivity extends SherlockFragmentActivity
        implements ErrorDialog.ErrorDialogCallback {

    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;

    CameraController mCameraController;

    CameraManager mCameraManager;

    private Uri mFileUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCameraManager = new CameraManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            mFileUri = mCameraManager
                    .getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri); // set the image file name

            // start the image capture Intent
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
            ErrorDialog errorDialog = ErrorDialog.newInstance("Error",
                    "Could not access SD Card. Please ensure that it is mounted");
            errorDialog.setCallback(this);
            errorDialog.show(getSupportFragmentManager(), "error_dialog");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            Logger.log(Logger.LogLevel.DEBUG, "onActivityResult: RESULT_OK");
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture close the app
            Logger.log(Logger.LogLevel.DEBUG, "onActivityResult: RESULT_CANCELED");
            finish();
        } else {
            Logger.log(Logger.LogLevel.DEBUG, "onActivityResult: NO RESULT SHOWN");
            ErrorDialog errorDialog = ErrorDialog.newInstance("Error",
                    "Unknown Result Code " + resultCode);
            errorDialog.setCallback(this);
            errorDialog.show(getSupportFragmentManager(), "error_dialog");
        }
    }

    /**
     * When the the user closes the dialog close the application
     */
    @Override
    public void onErrorDialogDismissed() {
        finish();
    }
}
