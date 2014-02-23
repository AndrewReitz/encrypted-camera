// Generated code from Butter Knife. Do not modify!
package com.andrewreitz.encryptedcamera.dialog;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SetPasswordDialog$$ViewInjector {
  public static void inject(Finder finder, final com.andrewreitz.encryptedcamera.dialog.SetPasswordDialog target, Object source) {
    View view;
    view = finder.findById(source, 2131361794);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131361794' for field 'passwordEditText' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.passwordEditText = (android.widget.EditText) view;
    view = finder.findById(source, 2131361795);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131361795' for field 'passwordEditTextConfirm' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.passwordEditTextConfirm = (android.widget.EditText) view;
  }

  public static void reset(com.andrewreitz.encryptedcamera.dialog.SetPasswordDialog target) {
    target.passwordEditText = null;
    target.passwordEditTextConfirm = null;
  }
}
