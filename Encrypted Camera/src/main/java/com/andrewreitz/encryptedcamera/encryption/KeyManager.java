package com.andrewreitz.encryptedcamera.encryption;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author areitz
 */
public class KeyManager {

    private final Context context;

    public KeyManager(Context context) {
        this.context = checkNotNull(context);
    }

    public void createKeyStore() {

    }

    public SecretKey generateKeyWithPassword(char[] passphraseOrPin, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }


    public void test() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        SecretKey key = KeyGenerator.getInstance("AES").generateKey();

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, "test".toCharArray());

        KeyStore.PasswordProtection pass = new KeyStore.PasswordProtection("fedsgjk".toCharArray());
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
        ks.setEntry("secretKeyAlias", skEntry, pass);

        FileOutputStream fos = context.openFileOutput("bs.keystore", Context.MODE_PRIVATE);
        ks.store(fos, "test".toCharArray());
        fos.close();
    }

    private SecretKey generateKeyNoPassword() throws NoSuchAlgorithmException {
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        return keyGenerator.generateKey();
    }
}
