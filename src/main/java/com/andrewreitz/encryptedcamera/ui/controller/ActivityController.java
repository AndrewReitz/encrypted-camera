package com.andrewreitz.encryptedcamera.ui.controller;

import android.app.ActionBar;
import android.app.Activity;

import org.jetbrains.annotations.NotNull;

public class ActivityController {
    private final Activity activity;

    public ActivityController(@NotNull Activity activity) {
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
