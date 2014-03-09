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

import android.content.SharedPreferences;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/** Service to make accessing shared preferences a little easier. */
public class DefaultSharedPreferenceService implements SharedPreferenceService {

    private final SharedPreferences mSharedPreferences;

    /**
     * Constructor: Should be instantiated with Dagger
     *
     * @param sharedPreferences shared preferences to use
     */
    public DefaultSharedPreferenceService(SharedPreferences sharedPreferences) {
        mSharedPreferences = checkNotNull(sharedPreferences);
    }

    /**
     * Saves boolean user data in shared preferences.
     *
     * @param key   value to save and get on
     * @param value value to store
     */
    @Override
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor prefs = mSharedPreferences.edit();
        prefs.putBoolean(key, value);
        prefs.commit();
    }

    /**
     * gets boolean value from stored preferences
     *
     * @param key        key to look value up on
     * @param defaultVal value returned if no value is found
     * @return value stored with key if found, defaultVal otherwise
     */
    @Override
    public boolean getBoolean(String key, boolean defaultVal) {
        return mSharedPreferences.getBoolean(key, defaultVal);
    }

    /**
     * Saves int user data in shared preferences.
     *
     * @param key   value to save and get on
     * @param value value to store
     */
    @Override
    public void saveInt(String key, int value) {
        SharedPreferences.Editor prefs = mSharedPreferences.edit();
        prefs.putInt(key, value);
        prefs.commit();
    }

    /**
     * gets int value from stored preferences
     *
     * @param key        key to look value up on
     * @param defaultVal value returned if no value is found
     * @return value stored with key if found, defaultVal otherwise
     */
    @Override
    public int getInt(String key, int defaultVal) {
        return mSharedPreferences.getInt(key, defaultVal);
    }

    /**
     * Saves float user data in shared preferences.
     *
     * @param key   value to save and get on
     * @param value value to store
     */
    @Override
    public void saveFloat(String key, float value) {
        SharedPreferences.Editor prefs = mSharedPreferences.edit();
        prefs.putFloat(key, value);
        prefs.commit();
    }

    /**
     * gets float value from stored preferences
     *
     * @param key        key to look value up on
     * @param defaultVal value returned if no value is found
     * @return value stored with key if found, defaultVal otherwise
     */
    @Override
    public float getFloat(String key, float defaultVal) {
        return mSharedPreferences.getFloat(key, defaultVal);
    }

    /**
     * Saves string user data in shared preferences.
     *
     * @param key   value to save and get on
     * @param value value to store
     */
    @Override
    public void saveString(String key, String value) {
        SharedPreferences.Editor prefs = mSharedPreferences.edit();
        prefs.putString(key, checkNotNull(value));
        prefs.commit();
    }

    /**
     * gets string value from stored preferences
     *
     * @param key        key to look value up on
     * @param defaultVal value returned if no value is found
     * @return value stored with key if found, defaultVal otherwise
     */
    @Override
    public String getString(String key, String defaultVal) {
        return mSharedPreferences.getString(key, defaultVal);
    }
}
