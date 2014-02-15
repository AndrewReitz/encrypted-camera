package com.andrewreitz.encryptedcamera.encryption;

import android.test.AndroidTestCase;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author areitz
 */
@SuppressWarnings("ConstantConditions")
public class KeyManagerImplTest extends AndroidTestCase {

    private String KEY_STORE_NAME = "test.keystore";
    private KeyManager keyManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        keyManager = new KeyManagerImpl(KEY_STORE_NAME, getContext());
    }

    public void testSaveKeyStore() throws Exception {
        keyManager.saveKeyStore();
        File file = getContext().getFileStreamPath(KEY_STORE_NAME);
        assertThat(file).exists();
    }

    public void testShouldGenerateKeySaveKeyAndGetKey() throws Exception {
        // Arrange
        String alias = "test";

        // Act
        SecretKey secretKey = keyManager.generateKeyNoPassword();
        keyManager.saveKey(alias, secretKey);
        SecretKey key = keyManager.getKey(alias);

        // Assert
        assertThat(key.getAlgorithm()).isEqualTo(secretKey.getAlgorithm());
        assertThat(key.getEncoded()).isEqualTo(secretKey.getEncoded());
        assertThat(key.getFormat()).isEqualTo(secretKey.getFormat());
    }

    public void testShouldGenerateKeySaveKeyWithAPasswordAndGetKeyWithPassword() throws Exception {
        // Arrange
        String password = "immasupersecretpassword";
        String alias = "test";

        // Act
        SecretKey secretKey = keyManager.generateKeyNoPassword();
        keyManager.saveKey(alias, secretKey, password);
        SecretKey key = keyManager.getKey(alias, password);

        // Assert
        assertThat(key.getAlgorithm()).isEqualTo(secretKey.getAlgorithm());
        assertThat(key.getEncoded()).isEqualTo(secretKey.getEncoded());
        assertThat(key.getFormat()).isEqualTo(secretKey.getFormat());
    }
}
