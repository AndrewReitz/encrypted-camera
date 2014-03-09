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

package com.andrewreitz.encryptedcamera.bus;

import org.jetbrains.annotations.NotNull;

/** Class to post events about whether or not the application is currently decrypting */
public class EncryptionEvent {

    public final EncryptionState state;

    public EncryptionEvent(@NotNull EncryptionState state) {
        this.state = state;
    }

    public enum EncryptionState {
        ENCRYPTING,
        DECRYPTING,
        NONE
    }
}
