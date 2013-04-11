package co.nodeath.encryptedcamera.business.encryption;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.Security;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Andrew
 */
@RunWith(RobolectricTestRunner.class)
public class AesEncryptionProviderTest {

    AesEncryptionProvider mAesEncryptionProvider;

    @BeforeClass
    public static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Before
    public void setup() {
        mAesEncryptionProvider = new AesEncryptionProvider("I'm a salt");
    }

    @Test
    public void shouldEncrypt() {
        //Arrange
        String value = "test";
        String expected = "7UNKNHJamhq3PohukUGSLw==";

        //Act
        String output = mAesEncryptionProvider.encrypt(value);

        //Assert
        assertNotNull(output);
        assertEquals(expected, output);
    }

    @Test
    public void shouldDecrypt() {
        //Arrange
        String expected = "test";
        String value = "7UNKNHJamhq3PohukUGSLw==";

        //Act
        String output = mAesEncryptionProvider.decrypt(value);

        //Assert
        assertNotNull(output);
        assertEquals(expected, output);
    }

    @Test
    public void shouldEncryptNullValue() {
        //Arrange
        String value = null;
        String expected = "w+3BEjjXsEbXGv6kIWYUEw==";

        //Act
        String output = mAesEncryptionProvider.encrypt(value);

        assertNotNull(output);
        assertEquals(expected, output);
    }

    @Test
    public void shouldDecryptNullValue() {
        //Arrange
        String expected = "";
        String value = "w+3BEjjXsEbXGv6kIWYUEw==";

        //Act
        String output = mAesEncryptionProvider.decrypt(value);

        assertNotNull(output);
        assertEquals(expected, output);
    }
}
