package com.andrewreitz.encryptedcamera.encryption;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

/**
 * @author areitz
 */
public interface KeyManager {
    void saveKeyStore() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException;

    void saveKey(String alias, SecretKey key) throws KeyStoreException;

    void saveKey(String alias, SecretKey key, String password) throws KeyStoreException;

    SecretKey getKey(String alias) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException;

    SecretKey getKey(String alias, String password) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException;

    SecretKey generateKey() throws NoSuchAlgorithmException;
}
