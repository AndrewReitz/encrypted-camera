package com.andrewreitz.encryptedcamera;

import android.app.Application;
import android.content.Context;

import com.andrewreitz.encryptedcamera.dependencyinjection.module.AndroidModule;
import com.andrewreitz.encryptedcamera.dialog.ErrorDialog;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.sharedpreference.EncryptedCameraPreferenceManager;

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

/**
 * @author areitz
 */
public class EncryptedCameraApp extends Application {
    public static final String KEY_STORE_ALIAS = EncryptedCameraApp.class.getSimpleName() + ":ALIAS";
    public static final String ENCRYPTED_DIRECTORY = "Encrypted";
    public static final String DECRYPTED_DIRECTORY = "Decrypted";
    public static final String MEDIA_OUTPUT_DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Inject
    EncryptedCameraPreferenceManager preferenceManager;

    @Inject
    KeyManager keyManager;

    private ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
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

        // Generate an encryption key if there isn't one already
        generateKey();
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
}
