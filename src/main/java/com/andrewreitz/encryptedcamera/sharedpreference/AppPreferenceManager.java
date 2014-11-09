/*
 * Copyright (C) 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andrewreitz.encryptedcamera.sharedpreference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.di.annotation.ForApplication;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class AppPreferenceManager {

  private static final String SALT = "salt";
  private static final String GENERATED_KEY = "generated_key";
  private static final String PASSWORD_HASH = "password_hash";
  private static final String HAS_SEEN_FIRST_LAUNCH_FRAGMENT = "has_seen_first_launch_frag";
  private static final String IV = "iv";
  private static final String HAS_SEEN_EXTERNAL_LAUNCH_WARNING = "external_launch";

  /** The required length of the IV for encrypting */
  private static final int IV_LENGTH = 16;

  private final Context context;
  private final SharedPreferenceService sharedPreferenceService;
  private final SecureRandom secureRandom;

  @Inject
  public AppPreferenceManager(@NonNull @ForApplication Context context,
      @NonNull SharedPreferenceService sharedPreferenceService,
      @NonNull SecureRandom secureRandom) {
    this.context = context;
    this.sharedPreferenceService = sharedPreferenceService;
    this.secureRandom = secureRandom;
  }

  public void setHasPassword(boolean hasPassword) {
    sharedPreferenceService.saveBoolean(context.getString(R.string.pref_key_use_password),
        hasPassword);
  }

  public boolean hasPassword() {
    return sharedPreferenceService.getBoolean(context.getString(R.string.pref_key_use_password),
        false);
  }

  public boolean isDecrypted() {
    return sharedPreferenceService.getBoolean(context.getString(R.string.pref_key_decrypt), false);
  }

  public byte[] getSalt() {
    String salt = sharedPreferenceService.getString(SALT, null);
    if (salt == null) {
      throw new IllegalStateException("You never set the salt...");
    }

    return Base64.decode(salt, Base64.DEFAULT);
  }

  public void setSalt(byte[] salt) {
    sharedPreferenceService.saveString(SALT, Base64.encodeToString(salt, Base64.DEFAULT));
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
  public void setPassword(@NonNull String password) {
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

  public byte[] getIv() {
    String iv = sharedPreferenceService.getString(IV, null);
    if (iv == null) {
      byte[] ivBytes = new byte[IV_LENGTH];
      secureRandom.nextBytes(ivBytes);
      iv = Base64.encodeToString(ivBytes, Base64.DEFAULT);
      sharedPreferenceService.saveString(IV, iv);
    }
    return Base64.decode(iv, Base64.DEFAULT);
  }

  public void setHasSeenExternalLaunchWarning(boolean value) {
    sharedPreferenceService.saveBoolean(HAS_SEEN_EXTERNAL_LAUNCH_WARNING, value);
  }

  public boolean hasSeenExternalLaunchWarning() {
    return sharedPreferenceService.getBoolean(HAS_SEEN_EXTERNAL_LAUNCH_WARNING, false);
  }
}
