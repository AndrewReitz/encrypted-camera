package com.andrewreitz.encryptedcamera.exception;

import java.io.IOException;

/**
 * Signals an error about the sd card
 *
 * @author Andrew
 */
public class SDCardException extends IOException {

    public SDCardException(String detailedMessage) {
        super(detailedMessage);
    }
}
