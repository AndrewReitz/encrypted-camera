package co.nodeath.encryptedcamera.presentation.controller;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;

import co.nodeath.encryptedcamera.business.exception.SDCardException;
import co.nodeath.encryptedcamera.presentation.dialog.ErrorDialog;

/**
 * All controllers should extend this class, keeps common code
 *
 * @author Andrew
 */
public abstract class AbstractController {

    private Activity mActivity;

    /**
     * The activity this controller controls.
     */
    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    /**
     * Call this from other controllers to save member variables
     *
     * @param activity activity that this controller controls
     */
    public void onCreate(final Activity activity) {
        setActivity(activity);
    }

    /**
     * Go to an activity
     *
     * @param aClass The class of the activity to go to
     */
    public void goToActivity(Class<?> aClass) {

        if (mActivity == null) {
            throw new IllegalStateException(
                    "onCreate must be called before this method may be called");
        }

        mActivity.startActivity(new Intent(mActivity, aClass));
    }

    /**
     * Go to an activity, calls finish on current activity so the back button will not navigate back
     * to it
     *
     * @param aClass The class of the activity to navigate to
     */
    public void goToActivityNoBackStack(Class<?> aClass) {
        this.goToActivity(aClass);
        mActivity.finish();
    }

    /**
     * @return the support fragment manager for the activity this controller controls
     */
    public FragmentManager getSupportFragmentManager() {
        return mActivity.getFragmentManager();
    }

    /**
     * Show an error dialog, activity must implement ErrorDialogListener
     *
     * @param title   title of the error dialog
     * @param message the message this error dialog displays
     */
    public void showErrorDialog(String title, String message) {

        ErrorDialog errorDialog = ErrorDialog.newInstance(title, message);
        errorDialog.show(getSupportFragmentManager(), "error_dialog");
    }

    /**
     * Handles errors and displays them to the user.  Your activity must implement
     * ErrorDialogListener in order to use this
     *
     * @param e exception to be handled and correct error message to be displayed
     */
    public void handleError(Exception e) {
        if (e instanceof SDCardException) {
            showErrorDialog("Error", "Could not access SD Card. Please ensure that it is mounted");
        }
    }
}
