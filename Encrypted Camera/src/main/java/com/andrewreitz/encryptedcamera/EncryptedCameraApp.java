package com.andrewreitz.encryptedcamera;

import android.app.Application;
import android.content.Context;

import com.andrewreitz.encryptedcamera.dependencyinjection.module.AndroidModule;
import com.andrewreitz.encryptedcamera.encryption.KeyManager;
import com.andrewreitz.encryptedcamera.sharedpreference.EncryptedCameraPreferenceManager;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * @author areitz
 */
public class EncryptedCameraApp extends Application {
    public static final String KEY_STORE_ALIAS = EncryptedCameraApp.class.getSimpleName() + ":ALIAS";

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
}
