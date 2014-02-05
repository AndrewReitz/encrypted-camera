package com.andrewreitz.encryptedcamera.encryption;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
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
    private static final String KEY_STORE_FILE = "app.keystore";

    private final Context context;
    private final KeyStore keyStore;

    public KeyManagerImpl(Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        this.context = checkNotNull(context);
        keyStore = getKeyStore();
    }

    private KeyStore getKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        File file = context.getFileStreamPath(KEY_STORE_FILE);
        if (file.exists()) {
            ks.load(context.openFileInput(KEY_STORE_FILE), null);
        } else {
            ks.load(null, null);
        }
        return ks;
    }

    @Override
    public void saveKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        FileOutputStream fos = context.openFileOutput(KEY_STORE_FILE, Context.MODE_PRIVATE);
        keyStore.store(fos, null);
        fos.close();
    }

    @Override
    public void saveKey(String alias, Key key) throws KeyStoreException {
        keyStore.setKeyEntry(
                checkNotNull(alias),
                checkNotNull(key).getEncoded(),
                null
        );
    }

    @Override
    public Key getKey(String alias) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return keyStore.getKey(alias, null);
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
