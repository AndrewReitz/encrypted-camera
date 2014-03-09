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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.andrewreitz.encryptedcamera.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
