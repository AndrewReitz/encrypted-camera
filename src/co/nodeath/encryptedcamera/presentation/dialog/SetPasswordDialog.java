package co.nodeath.encryptedcamera.presentation.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import co.nodeath.encryptedcamera.R;

/**
 * @author Andrew
 */
public class SetPasswordDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    private SetPasswordDialogListener mSetPasswordDialogListener;

    public static SetPasswordDialog NewInstance() {
        return new SetPasswordDialog();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mSetPasswordDialogListener = (SetPasswordDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.getClass().getName() + " must implement SetPasswordDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_set_password, null))
                .setTitle(activity.getString(R.string.set_password))
                .setMessage(activity.getString(R.string.do_not_loose_password))
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                AlertDialog alertDialog = (AlertDialog) dialog;
                String password = ((EditText) alertDialog
                        .findViewById(R.id.dialog_editText_password)).getText().toString();
                String confirmPassword = ((EditText) alertDialog
                        .findViewById(R.id.dialog_editText_password_confirm)).getText().toString();
                mSetPasswordDialogListener.handleSetPassword(password,
                        confirmPassword);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mSetPasswordDialogListener.passwordCancelled();
                break;
            default:
                throw new IllegalStateException("Unknown button press " + which);
        }
    }

    public interface SetPasswordDialogListener {
        public void handleSetPassword(String password, String confirmPassword);
        public void passwordCancelled();
    }
}