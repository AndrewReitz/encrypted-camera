package co.nodeath.encryptedcamera.presentation.dialog;

import android.app.Activity;
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
public class ErrorDialog extends DialogFragment {

    private static final String TITLE = "error_dialog_title";

    private static final String MESSAGE = "error_dialog_message";

    private String mTitle;

    private String mMessage;

    private ErrorDialogListener mListener;

    /**
     * Convenience method for getting access to this dialog
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ErrorDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.getClass().getName()
                    + " must implement ErrorDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TITLE)) {
                mTitle = savedInstanceState.getString(TITLE);
            }
            if (savedInstanceState.containsKey(MESSAGE)) {
                mMessage = savedInstanceState.getString(MESSAGE);
            }
        } else {
            final Bundle args = getArguments();

            mTitle = args.getString(TITLE);
            mMessage = args.getString(MESSAGE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE, mTitle);
        outState.putString(MESSAGE, mMessage);
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

        builder.setPositiveButton(context.getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onErrorDialogDismissed(dialog);
                    }
                });

        final Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public interface ErrorDialogListener {

        /**
         * Called when user clicks the ok button on the error message
         *
         * @param dialog passes the dialog that is being displayed
         */
        public void onErrorDialogDismissed(DialogInterface dialog);
    }
}
