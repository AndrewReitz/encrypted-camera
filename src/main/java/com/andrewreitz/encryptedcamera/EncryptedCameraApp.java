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

package com.andrewreitz.encryptedcamera;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.andrewreitz.encryptedcamera.bus.EncryptionEvent;
import com.andrewreitz.encryptedcamera.di.module.AndroidModule;
import com.andrewreitz.encryptedcamera.sharedpreference.AppPreferenceManager;
import com.andrewreitz.encryptedcamera.ui.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;
import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import timber.log.Timber;

public class EncryptedCameraApp extends Application {
    public static final String KEY_STORE_ALIAS = EncryptedCameraApp.class.getSimpleName() + ":ALIAS";
    public static final String ENCRYPTED_DIRECTORY = "Encrypted";
    public static final String DECRYPTED_DIRECTORY = "Decrypted";
    public static final String MEDIA_OUTPUT_DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Inject AppPreferenceManager preferenceManager;
    @Inject KeyManager keyManager;
    @Inject Bus bus;
    @Inject LruCache<String, Bitmap> cache;

    private ObjectGraph applicationGraph;
    private EncryptionEvent.EncryptionState lastSate = EncryptionEvent.EncryptionState.NONE;

    @Override public void onCreate() {
        super.onCreate();

        // Logging Setup
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Setup debugging for butterknife
        ButterKnife.setDebug(BuildConfig.DEBUG);

        // Setup DI
        applicationGraph = ObjectGraph.create(getModules().toArray());
        applicationGraph.inject(this);

        // Get on the bus!
        bus.register(this);

        // Generate an encryption key if there isn't one already
        generateKey();
    }

    @Override
    public void onTrimMemory(int level) {
        Timber.i("onTrimMemory() with level=%s", level);

        // Memory we can release here will help overall system performance, and
        // make us a smaller target as the system looks for memory

        if (level >= Application.TRIM_MEMORY_MODERATE) { // 60
            // Nearing middle of list of cached background apps; evict our
            // entire thumbnail cache
            Timber.i("evicting entire thumbnail cache");
            cache.evictAll();

        } else if (level >= Application.TRIM_MEMORY_BACKGROUND) { // 40
            // Entering list of cached background apps; evict oldest half of our
            // thumbnail cache
            Timber.i("evicting oldest half of thumbnail cache");
            cache.trimToSize(cache.size() / 2);
        }
    }

    /**
     * A list of modules to use for the application graph. Subclasses can override this method to
     * provide additional modules provided they call {@code super.getModules()}.
     */
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new AndroidModule(this));
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }

    public void inject(Object o) {
        applicationGraph.inject(o);
    }

    public static EncryptedCameraApp get(Context context) {
        return (EncryptedCameraApp) context.getApplicationContext();
    }

    private void generateKey() {
        if (!preferenceManager.hasGeneratedKey()) {
            try {
                SecretKey secretKey = keyManager.generateKeyNoPassword();
                keyManager.saveKey(KEY_STORE_ALIAS, secretKey);
                keyManager.saveKeyStore();
                preferenceManager.setGeneratedKey(true);
            } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException e) {
                Timber.e(e, "Error saving key with out a password set");
                ErrorDialog.newInstance(getString(R.string.encryption_error), getString(R.string.error_saving_encryption_key));
            }
        }
    }

    @Subscribe public void answerEncryptionEvent(EncryptionEvent event) {
        lastSate = event.state;
    }

    @Produce public EncryptionEvent produceEncryptionEvent() {
        return new EncryptionEvent(lastSate);
    }
}
