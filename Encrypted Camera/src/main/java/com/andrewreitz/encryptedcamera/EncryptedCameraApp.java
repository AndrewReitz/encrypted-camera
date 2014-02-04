package com.andrewreitz.encryptedcamera;

import android.app.Application;
import android.content.Context;

import com.andrewreitz.encryptedcamera.dependencyinjection.module.AndroidModule;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
public class EncryptedCameraApp extends Application {

    private ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        // Logging Setup
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // Add BouncyCastleProvider to encryption providers
        Security.addProvider(new BouncyCastleProvider());

        // Setup DI
        applicationGraph = ObjectGraph.create(getModules().toArray());
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
        return (EncryptedCameraApp) checkNotNull(context).getApplicationContext();
    }
}
