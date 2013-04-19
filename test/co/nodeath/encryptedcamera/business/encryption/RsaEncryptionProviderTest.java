package co.nodeath.encryptedcamera.business.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author Andrew
 */
public class RsaEncryptionProviderTest {

    private static KeyPair mKey;

    private RsaEncryptionProvider mRsaEncryptionProvider;

    @BeforeClass
    public static void init() {
        Security.addProvider(new BouncyCastleProvider());
        mKey = RsaEncryptionProvider.generateKey();
    }

    @Before
    public void setup() {
        mRsaEncryptionProvider = new RsaEncryptionProvider(mKey);
    }

    @Test
    public void shouldEncrypt() {
        //Arrange
        String value = "test";

        //Act
        String encrypted = mRsaEncryptionProvider.encrypt(value);
        String decrypted = mRsaEncryptionProvider.decrypt(encrypted);

        //Assert
        assertNotNull(encrypted);
        assertEquals(value, decrypted);
    }
}
