package com.andrewreitz.encryptedcamera.encryption;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
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
public class KeyManagerImpl implements KeyManager {
    private final Context context;
    private final KeyStore keyStore;
    private String keyStoreName = "app.keystore";

    public KeyManagerImpl(Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        this.context = checkNotNull(context);
        keyStore = getKeyStore();
    }

    public KeyManagerImpl(String keystoreName, Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        this.context = checkNotNull(context);
        keyStore = getKeyStore();
        if (keystoreName != null) this.keyStoreName = keystoreName;
    }

    private KeyStore getKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType()); //"AndroidKeyStore"
        File file = context.getFileStreamPath(keyStoreName);
        if (file.exists()) {
            ks.load(context.openFileInput(keyStoreName), null);
        } else {
            ks.load(null, null);
        }
        return ks;
    }

    @Override
    public void saveKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        FileOutputStream fos = context.openFileOutput(keyStoreName, Context.MODE_PRIVATE);
        keyStore.store(fos, null);
        fos.close();
    }

    @Override
    public void saveKey(String alias, SecretKey key) throws KeyStoreException {
        keyStore.setKeyEntry(
                checkNotNull(alias),
                key,
                null,
                null
        );
    }

    @Override
    public SecretKey getKey(String alias) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (SecretKey) keyStore.getKey(alias, null);
    }

    @Override
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
        return secretKeyFactory.generateSecret(keySpec);
    }

    @Override
    public SecretKey generateKeyNoPassword() throws NoSuchAlgorithmException {
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        return keyGenerator.generateKey();
    }
}
