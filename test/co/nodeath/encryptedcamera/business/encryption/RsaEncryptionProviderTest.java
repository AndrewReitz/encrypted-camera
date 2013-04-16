package co.nodeath.encryptedcamera.business.encryption;

import junit.framework.Assert;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.Security;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Andrew
 */
public class RsaEncryptionProviderTest {

    private RsaEncryptionProvider mRsaEncryptionProvider;

    @BeforeClass
    public static void init()  {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Before
    public void setup() {
        mRsaEncryptionProvider = new RsaEncryptionProvider();
    }

    @Test
    public void shouldEncrypt() {
        //Arrange
        String value = "test";

        //Act
        byte[] out = mRsaEncryptionProvider.encrypt(value.getBytes());

        //Assert
        assertNotNull(out);
    }
}
