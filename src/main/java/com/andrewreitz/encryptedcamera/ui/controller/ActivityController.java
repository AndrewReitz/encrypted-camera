package com.andrewreitz.encryptedcamera.ui.controller;

import android.app.ActionBar;
import android.app.Activity;
import android.support.annotation.NonNull;

public class ActivityController {
  private final Activity activity;

  public ActivityController(@NonNull Activity activity) {
    this.activity = activity;
  }

  public void finish() {
    activity.finish();
  }

  public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
    ActionBar actionBar = activity.getActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }
}
