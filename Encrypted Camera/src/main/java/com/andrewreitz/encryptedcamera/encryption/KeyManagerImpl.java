package com.andrewreitz.encryptedcamera.encryption;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private String keyStoreName = "app.keystore"; // default name

    public KeyManagerImpl(@NotNull Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        this.context = checkNotNull(context);
        keyStore = getKeyStore();
    }

    public KeyManagerImpl(@Nullable String keystoreName, @NotNull Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
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
    public void saveKey(@NotNull String alias, @NotNull SecretKey key) throws KeyStoreException {
        this.saveKey(alias, key, null);
    }

    @Override
    public void saveKey(@NotNull String alias, @NotNull SecretKey key, @Nullable String password) throws KeyStoreException {
        keyStore.setKeyEntry(
                checkNotNull(alias),
                key,
                password == null ? null : password.toCharArray(),
                null
        );
    }

    @Override
    public SecretKey getKey(@NotNull String alias) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return this.getKey(alias, null);
    }

    @Override
    public SecretKey getKey(@NotNull String alias, @Nullable String password) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (SecretKey) keyStore.getKey(alias, password == null ? null : password.toCharArray());
    }

    @Override
    public SecretKey generateKey() throws NoSuchAlgorithmException {
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        return keyGenerator.generateKey();
    }
}
