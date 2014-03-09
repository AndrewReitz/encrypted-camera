/*
 * Copyright (C) 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andrewreitz.encryptedcamera.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.andrewreitz.encryptedcamera.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SetPasswordDialog extends DialogFragment implements DialogInterface.OnClickListener, DialogInterface.OnShowListener {

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
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                // handled before default handler
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                handleNegetiveButtonPress();
                break;
            default:
                throw new IllegalArgumentException("Unknown button pressed, which == " + which);
        }
    }

    private void handleNegetiveButtonPress() {
        this.listener.onPasswordSetCancel();
        //noinspection ConstantConditions
        SetPasswordDialog.this.getDialog().cancel();
        this.listener.onPasswordSetCancel();
    }

    private boolean handlePostitiveButtonPress() {
        //noinspection ConstantConditions
        String password = passwordEditText.getText().toString();
        boolean isNullOrEmptyPassword = TextUtils.isEmpty(password);
        //noinspection ConstantConditions
        if (!isNullOrEmptyPassword && password.equals(passwordEditTextConfirm.getText().toString())) {
            listener.onPasswordSet(password);
            return true;
        } else {
            passwordEditText.setText("");
            passwordEditTextConfirm.setText("");
            if (isNullOrEmptyPassword) {
                passwordEditText.setError(getString(R.string.password_can_not_be_empty));
            } else {
                passwordEditText.setError(getString(R.string.passwords_not_match));
            }
            return false;
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onShow(final DialogInterface dialog) {
        AlertDialog alertDialog = (AlertDialog) dialog;
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handlePostitiveButtonPress()) dialog.dismiss();
            }
        });
    }

    public interface SetPasswordDialogListener {
        void onPasswordSet(String password);

        void onPasswordSetCancel();
    }
}
