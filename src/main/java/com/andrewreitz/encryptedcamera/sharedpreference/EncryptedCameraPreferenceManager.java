package com.andrewreitz.encryptedcamera.sharedpreference;

import android.content.Context;
import android.util.Base64;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.dependencyinjection.annotation.ForApplication;

import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
public class EncryptedCameraPreferenceManager {

    private static final String SALT = "salt";
    private static final String GENERATED_KEY = "generated_key";
    private static final String PASSWORD_HASH = "password_hash";
    private static final String IS_DECRYPTING = "is_decrypting";

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

    public boolean isDecrypted() {
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

    public void setIsDecrypting(boolean isDecrypting) {
        sharedPreferenceService.saveBoolean(IS_DECRYPTING, isDecrypting);
    }

    public boolean isDecrypting() {
        return sharedPreferenceService.getBoolean(IS_DECRYPTING, false);
    }

    /**
     * Stores the password as a hash. Using BCrypt!
     *
     * @param password the password to store
     */
    public void setPassword(@NotNull String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(4)); // opting for speed here
        sharedPreferenceService.saveString(PASSWORD_HASH, hashed);
    }

    /**
     * return BCrypt hash password
     */
    public String getPasswordHash() {
        String hash = sharedPreferenceService.getString(PASSWORD_HASH, null);
        if (hash == null) {
            throw new RuntimeException("Password hash was never set.  Call setPassword first");
        }
        return hash;
    }
}
