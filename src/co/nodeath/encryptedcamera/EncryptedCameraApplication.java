package co.nodeath.encryptedcamera;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import android.app.Application;

import java.security.Security;

/**
 * @author Andrew
 */
public class EncryptedCameraApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Add BouncyCastleProvider to encryption providers
        Security.addProvider(new BouncyCastleProvider());
    }
}
