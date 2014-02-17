package com.andrewreitz.encryptedcamera.sharedpreference;

import android.content.Context;
import android.util.Base64;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
public class EncryptedCameraPreferenceManager {

    private static final String SALT = "salt";
    private static final String GENERATED_KEY = "generated_key";

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

    public boolean hasPassword() {
        return sharedPreferenceService.getBoolean(
                context.getString(R.string.pref_key_use_password),
                false
        );
    }

    public boolean getDecrypted() {
        return sharedPreferenceService.getBoolean(
                context.getString(R.string.pref_key_decrypt),
                false
        );
    }

    public void setSalt(byte[] salt) {
        sharedPreferenceService.saveString(SALT, Base64.encodeToString(salt, Base64.DEFAULT));
    }

    public byte[] getSalt() {
        String salt = sharedPreferenceService.getString(SALT, null);
        if (salt == null) {
            throw new IllegalStateException("You never set the salt...");
        }

        return Base64.decode(salt, Base64.DEFAULT);
    }

    /**
     * check to see if a key has been generated
     */
    public boolean hasGeneratedKey() {
        return sharedPreferenceService.getBoolean(GENERATED_KEY, false);
    }

    public void setGeneratedKey(boolean generated) {
        sharedPreferenceService.saveBoolean(GENERATED_KEY, generated);
    }
}
