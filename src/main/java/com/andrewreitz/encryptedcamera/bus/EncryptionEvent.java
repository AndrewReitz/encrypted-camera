package com.andrewreitz.encryptedcamera.bus;

import org.jetbrains.annotations.NotNull;

/** Class to post events about whether or not the application is currently decrypting */
public class EncryptionEvent {

    public final EncryptionState state;

    public EncryptionEvent(@NotNull EncryptionState state) {
        this.state = EncryptionState.NONE;
    }

    public enum EncryptionState {
        ENCRYPTING,
        DECRYPTING,
        NONE
    }
}
