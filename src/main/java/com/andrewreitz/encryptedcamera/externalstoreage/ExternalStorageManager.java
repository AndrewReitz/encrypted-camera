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

package com.andrewreitz.encryptedcamera.externalstoreage;

import android.net.Uri;

import com.andrewreitz.encryptedcamera.exception.SDCardException;
import com.google.common.net.MediaType;

import java.io.File;

/**
 * @author areitz
 */
public interface ExternalStorageManager {
    Uri getOutputMediaFileUri(MediaType type) throws SDCardException;

    File getOutputMediaFile(MediaType type) throws SDCardException;

    Uri getHiddenOutputMediaFileUri(MediaType type) throws SDCardException;

    File getHiddenOutputMediaFile(MediaType type) throws SDCardException;

    File getAppExternalDirectory();

    File getHiddenAppExternalDirectory();

    boolean checkSdCardIsInReadAndWriteState();
}
