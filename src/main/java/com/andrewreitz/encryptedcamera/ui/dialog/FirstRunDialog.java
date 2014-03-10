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

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.ui.activity.AboutActivity;

public class FirstRunDialog extends DialogFragment implements AlertDialog.OnClickListener {

    public static FirstRunDialog newInstance() {
        return new FirstRunDialog();
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions")
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_firstrun, null);
        builder.setView(view)
                .setTitle(getString(R.string.welcome))
                .setPositiveButton(R.string.got_it, null)
                .setNegativeButton(R.string.learn_more, this);
        return builder.create();
    }

    @Override public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                AboutActivity.navigateTo(getActivity());
                break;
            default:
                throw new IllegalArgumentException("Unknown button pressed, which == " + which);
        }
    }
}
