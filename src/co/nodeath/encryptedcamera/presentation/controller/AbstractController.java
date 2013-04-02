package co.nodeath.encryptedcamera.presentation.controller;

import android.app.Activity;
import android.content.Intent;

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
     * Pass in an exception to display an error message on the activity
     *
     * @param exception exception that was thrown
     */
    public void onError(Exception exception) {
        if (mActivity != null) {
            throw new IllegalStateException(
                    "Activity must be set using setActivity before this method can be called");
        }

        try {
            throw exception;
        } catch (Exception e) {
        }
    }
}
