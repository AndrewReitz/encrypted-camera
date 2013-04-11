package co.nodeath.encryptedcamera.presentation.dialog;

import com.actionbarsherlock.app.SherlockDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.widget.EditText;

import co.nodeath.encryptedcamera.R;

/**
 * @author Andrew
 */
public class SetPasswordDialog extends SherlockDialogFragment implements
        DialogInterface.OnClickListener {

    private EditText mPasswordEditText;

    private EditText mPasswordConfirmEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_set_password, null))
                .setTitle(activity.getString(R.string.set_password))
                .setMessage(activity.getString(R.string.do_not_loose_password))
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this);

        AlertDialog alertDialog = builder.create();

        mPasswordEditText = (EditText) alertDialog.findViewById(R.id.dialog_editText_password);
        mPasswordConfirmEditText = (EditText) alertDialog
                .findViewById(R.id.dialog_editText_password_confirm);

        return alertDialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.cancel();
                break;
            default:
                throw new IllegalStateException("Unknown button press " + which);
        }
    }

    public interface SetPasswordDialogListener {
        public void handleSetPassword(String password, String confirmPassword);
    }
}