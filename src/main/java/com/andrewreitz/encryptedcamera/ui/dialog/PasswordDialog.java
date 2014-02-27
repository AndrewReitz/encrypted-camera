package com.andrewreitz.encryptedcamera.ui.dialog;

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
public class PasswordDialog extends DialogFragment implements AlertDialog.OnClickListener, DialogInterface.OnShowListener {

    @InjectView(R.id.dialog_editText_password) EditText passwordEditText;

    private PasswordDialogListener listener;

    public static PasswordDialog newInstance(PasswordDialogListener passwordDialogListener) {
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.listener = passwordDialogListener;
        return passwordDialog;
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions")
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_password, null);
        ButterKnife.inject(this, view);
        builder.setView(view)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                //noinspection ConstantConditions
                listener.onPasswordEntered(passwordEditText.getText().toString());
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                listener.onPasswordCancel();
                break;
            default:
                throw new IllegalArgumentException("Unknown button pressed, which == " + which);
        }
    }

    @Override public void onShow(DialogInterface dialog) {

    }

    public interface PasswordDialogListener {
        void onPasswordEntered(String password);
        void onPasswordCancel();
    }
}
