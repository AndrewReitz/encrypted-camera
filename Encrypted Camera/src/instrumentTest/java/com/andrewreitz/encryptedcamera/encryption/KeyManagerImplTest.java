package com.andrewreitz.encryptedcamera.encryption;

import android.test.AndroidTestCase;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * @author areitz
 */
public class KeyManagerImplTest extends AndroidTestCase {

    KeyManager keyManager;

    public void setup() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        keyManager = new KeyManagerImpl(getContext());
    }

    public void shouldDoSomeStuff() {
        throw new RuntimeException("CRASHED");
    }
}
