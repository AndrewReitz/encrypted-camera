package com.andrewreitz.encryptedcamera.sharedpreference;

import android.content.Context;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
public class EncryptedCameraPreferenceManager {

    private static final String SALT = "salt";

    private final Context context;
    private final SharedPreferenceService sharedPreferenceService;

    @Inject
    public EncryptedCameraPreferenceManager(
            @ForApplication Context context,
            SharedPreferenceService sharedPreferenceService
    ) {
        this.context = checkNotNull(context);
        this.sharedPreferenceService = checkNotNull(sharedPreferenceService);
    }

    public void setHasPassword(boolean hasPassword) {
        sharedPreferenceService.saveBoolean(
                context.getString(R.string.pref_key_use_password),
                hasPassword
        );
    }

    public void setSalt(String salt) {
        sharedPreferenceService.saveString(SALT, salt);
    }

    public String getSalt() {
        String salt = sharedPreferenceService.getString(SALT, null);
        if (salt == null) {
            throw new IllegalStateException("You never set the salt...");
        }

        return salt;
    }
}
