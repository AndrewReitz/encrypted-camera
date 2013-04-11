package co.nodeath.encryptedcamera.presentation.controller;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import co.nodeath.encryptedcamera.business.exception.SDCardException;
import co.nodeath.encryptedcamera.presentation.dialog.ErrorDialog;

/**
 * All controllers should extend this class, keeps common code
 *
 * @author Andrew
 */
public abstract class AbstractController {

    private FragmentActivity mActivity;

    /**
     * The activity this controller controls.
     */
    public FragmentActivity getActivity() {
        return mActivity;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    /**
     * Call this from other controllers to save member variables
     *
     * @param activity activity that this controller controls
     */
    public void onCreate(final FragmentActivity activity) {
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
        return mActivity.getSupportFragmentManager();
    }

    /**
     * Show an error dialog
     *
     * @param title   title of the error dialog
     * @param message the message this error dialog displays
     */
    public void showErrorDialog(String title, String message) {
        showErrorDialog(title, message, null);
    }

    /**
     * Show an error dialog
     *
     * @param title               title of the error dialog
     * @param message             the message this error dialog displays
     * @param errorDialogCallback what happens when the error dialog is dismissed
     */
    public void showErrorDialog(String title, String message,
            ErrorDialog.ErrorDialogCallback errorDialogCallback) {

        ErrorDialog errorDialog = ErrorDialog.newInstance(title, message);

        if (errorDialogCallback != null) {
            errorDialog.setCallback(errorDialogCallback);
        }

        errorDialog.show(getSupportFragmentManager(), "error_dialog");
    }

    public void handleError(Exception e) {
        if (e instanceof SDCardException) {
            //TODO pass in callback
            showErrorDialog("Error", "Could not access SD Card. Please ensure that it is mounted",
                    null);
        }
    }
}
