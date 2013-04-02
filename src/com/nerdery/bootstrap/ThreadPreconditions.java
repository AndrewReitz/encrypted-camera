package com.nerdery.bootstrap;

import android.os.Looper;

import co.nodeath.encryptedcamera.BuildConfig;

/**
 * @author areitz
 */
public final class ThreadPreconditions {

    /**
     * Checks to see if you are executing on the main thread, if you are not it wil throw an
     * IllegalStateException so that you can fix it.
     *
     * Use this where ever you need your action to run on MainThread
     */
    public static void checkOnMainThread() {
        if (BuildConfig.DEBUG) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                throw new IllegalStateException(
                        "This method should be called from the Main Thread");
            }
        }
    }

    private ThreadPreconditions() {
        throw new AssertionError();
    }
}
