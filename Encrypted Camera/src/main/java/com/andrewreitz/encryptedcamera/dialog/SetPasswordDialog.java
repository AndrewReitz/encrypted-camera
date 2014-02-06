package com.andrewreitz.encryptedcamera.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.andrewreitz.encryptedcamera.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author areitz
 */
public class SetPasswordDialog extends DialogFragment implements DialogInterface.OnClickListener {

    @InjectView(R.id.dialog_editText_password)
    EditText passwordEditText;

    @InjectView(R.id.dialog_editText_password_confirm)
    EditText passwordEditTextConfirm;

    private SetPasswordDialogListener listener;

    public static SetPasswordDialog newInstance(SetPasswordDialogListener listener) {
        SetPasswordDialog setPasswordDialog = new SetPasswordDialog();
        setPasswordDialog.listener = listener;
        return setPasswordDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions")
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_set_password, null);
        ButterKnife.inject(this, view);
        builder.setView(view)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                handlePostitiveButtonPress();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                handleNegetiveButtonPress();
                break;
            default:
                throw new IllegalArgumentException("Unknown button pressed, which == " + which);
        }
    }

    private void handleNegetiveButtonPress() {
        this.listener.onPasswordCancel();
        //noinspection ConstantConditions
        SetPasswordDialog.this.getDialog().cancel();
        this.listener.onPasswordCancel();
    }

    private void handlePostitiveButtonPress() {
        //noinspection ConstantConditions
        String password = passwordEditText.getText().toString();
        //noinspection ConstantConditions
        if (password.equals(passwordEditTextConfirm.getText().toString())) {
            listener.onPasswordSet(password);
        } else {
            passwordEditTextConfirm.setError(getString(R.string.passwords_do_not_match));
        }
    }

    public interface SetPasswordDialogListener {
        void onPasswordSet(String password);
        void onPasswordCancel();
    }
}
