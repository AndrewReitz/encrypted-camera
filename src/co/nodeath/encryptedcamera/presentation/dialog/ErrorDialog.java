package co.nodeath.encryptedcamera.presentation.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Error Dialog for displaying error messages easily in the application
 *
 * @author Andrew
 */
public class ErrorDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String TITLE = "error_dialog_title";
    private static final String MESSAGE = "error_dialog_message";
    private String mTitle;
    private String mMessage;
    private ErrorDialogCallback mCallback;

    /**
     * Convience method for getting access to this dialog
     *
     * @param title   the title to display
     * @param message message to display
     * @return newly created ErrorDialog
     */
    public static ErrorDialog newInstance(final String title, final String message) {
        final ErrorDialog errorDialog = new ErrorDialog();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);

        errorDialog.setArguments(args);

        return errorDialog;
    }

    /**
     * Set the callback when the error is dismissed If this is not passed in nothing happens when
     * the ok button is pressed
     *
     * @param errorDialogCallback callback to set
     */
    public void setCallback(ErrorDialogCallback errorDialogCallback) {
        mCallback = errorDialogCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();

        mTitle = args.getString(TITLE);
        mMessage = args.getString(MESSAGE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = getActivity();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (!TextUtils.isEmpty(mTitle)) {
            builder.setTitle(mTitle);
        }

        if (!TextUtils.isEmpty(mMessage)) {
            builder.setMessage(mMessage);
        }

        builder.setPositiveButton(context.getString(android.R.string.ok), this);

        final Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (mCallback != null) {
            mCallback.onErrorDialogDismissed();
        }
    }

    public interface ErrorDialogCallback {

        public void onErrorDialogDismissed();
    }
}
