package com.andrewreitz.encryptedcamera.encryption;

import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * @author areitz
 */
public class KeyManagerImplTest extends AndroidTestCase {

    KeyManager keyManager;

    @Before
    void setup() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        keyManager = new KeyManagerImpl(getContext());
    }

    @Test void shouldDoSomeStuff() {
        throw new RuntimeException("CRASHED");
    }
}
