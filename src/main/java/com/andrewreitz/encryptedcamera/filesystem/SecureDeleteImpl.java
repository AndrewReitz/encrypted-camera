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

import android.support.annotation.NonNull;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

import static com.google.common.base.Preconditions.checkNotNull;

public class SecureDeleteImpl implements SecureDelete {

  private final SecureRandom secureRandom;

  public SecureDeleteImpl(@NonNull SecureRandom secureRandom) {
    this.secureRandom = checkNotNull(secureRandom);
  }

  @Override public boolean secureDelete(@NonNull File file) throws IOException {
    if (file.exists()) {
      long length = file.length();
      RandomAccessFile raf = new RandomAccessFile(file, "rws");
      raf.seek(0);
      raf.getFilePointer();
      byte[] data = new byte[1024];
      int pos = 0;
      while (pos < length) {
        secureRandom.nextBytes(data);
        raf.write(data);
        pos += data.length;
      }
      raf.close();
      return file.delete();
    }

    return true; // didn't exist so we technically deleted...
  }
}
