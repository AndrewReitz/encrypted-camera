package com.andrewreitz.encryptedcamera.sharedpreference;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
public class EncryptedCameraPreferenceManager {

    private static final String SHOULD_ENCRYPT = "should_encrypt";

    private final SharedPreferenceService sharedPreferenceService;

    @Inject
    public EncryptedCameraPreferenceManager(SharedPreferenceService sharedPreferenceService) {
        this.sharedPreferenceService = checkNotNull(sharedPreferenceService);
    }

    public void setShouldEncrypt(boolean encrypt) {
        this.sharedPreferenceService.saveBoolean(SHOULD_ENCRYPT, encrypt);
    }

    public boolean shouldEncrypt() {
        return this.sharedPreferenceService.getBoolean(SHOULD_ENCRYPT, false);
    }
}
