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

/**
 * Interface for creating different sharedpreferenceservices
 */
public interface SharedPreferenceService {

    public void saveBoolean(String key, boolean value);

    public boolean getBoolean(String key, boolean defaultVal);

    public void saveInt(String key, int value);

    public int getInt(String key, int defaultVal);

    public void saveFloat(String key, float value);

    public float getFloat(String key, float defaultVal);

    public void saveString(String key, String value);

    public String getString(String key, String defaultVal);
}
