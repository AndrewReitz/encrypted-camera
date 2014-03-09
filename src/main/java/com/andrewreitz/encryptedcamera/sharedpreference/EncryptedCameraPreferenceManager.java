/*
 *
 *  * Copyright (C) 2014 Andrew Reitz
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.andrewreitz.encryptedcamera.sharedpreference;

import android.content.Context;
import android.util.Base64;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.di.annotation.ForApplication;

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
    private static final String HAS_SEEN_FIRST_LAUNCH_FRAGMENT = "has_seen_first_launch_frag";

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

    public boolean hasSeenFirstRunFragment() {
        return sharedPreferenceService.getBoolean(HAS_SEEN_FIRST_LAUNCH_FRAGMENT, false);
    }

    public void setHasSeenFirstLaunchFragment(boolean value) {
        sharedPreferenceService.saveBoolean(HAS_SEEN_FIRST_LAUNCH_FRAGMENT, value);
    }
}
