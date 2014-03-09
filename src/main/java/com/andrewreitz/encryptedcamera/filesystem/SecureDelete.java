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

package com.andrewreitz.encryptedcamera.filesystem;

import java.io.File;
import java.io.IOException;

public interface SecureDelete {

    /**
     * Writes random bytes to the file then deletes it to make it unrecoverable.
     *
     * @param file the file to delete
     * @return true if the file was deleted or if the file never existed
     * @throws IOException
     */
    boolean secureDelete(File file) throws IOException;
}
