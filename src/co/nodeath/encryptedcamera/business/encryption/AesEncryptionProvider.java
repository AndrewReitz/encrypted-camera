package co.nodeath.encryptedcamera.business.encryption;

import org.bouncycastle.util.encoders.Base64;

import android.text.TextUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * @author Andrew
 */
public class AesEncryptionProvider implements IEncryptionProvider {

    private static final String PROVIDER = "BC";

    private static final String KEY_FACTORY = "PBKDF2WithHmacSHA1";

    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";

    private static final String SEKRIT = "PGh0bWw+PGhlYWQ+PG1ldGEgaHR0cC1lcXVpdj0iY29udGVudC10eX"
            + "BlIiBjb250ZW50PSJ0ZXh0L2h0bWw7IGNoYXJzZXQ9VVRGLTgiPjx0aXRsZT5SZWRpcmVjdCBOb3RpY2U8L3"
            + "RpdGxlPjxzdHlsZT48IS0tCmJvZHksdGQsZGl2LC5wLGF7Zm9udC1mYW1pbHk6YXJpYWwsc2Fucy1zZXJpZn"
            + "0KZGl2LHRke2NvbG9yOiMwMDB9Ci5me2NvbG9yOiM2ZjZmNmZ9CmE6bGlua3tjb2xvcjojMDBjfQphOnZpc2"
            + "l0ZWR7Y29sb3I6IzU1MWE4Yn0KYTphY3RpdmV7Y29sb3I6cmVkfQpkaXYuYXtib3JkZXItdG9wOjFweCBzb2"
            + "xpZCAjYmJiO2JvcmRlci1ib3R0b206MXB4IHNvbGlkICNiYmI7YmFja2dyb3VuZDojZjJmMmYyO21hcmdpbi"
            + "10b3A6MWVtO3dpZHRoOjEwMCV9CmRpdi5ie3BhZGRpbmc6MC41ZW0gMDttYXJnaW4tbGVmdDoxMHB4fQpkaX"
            + "YuY3ttYXJnaW4tdG9wOjM1cHg7bWFyZ2luLWxlZnQ6MzVweH0KLS0+PC9zdHlsZT4KPHNjcmlwdD5mdW5jdG"
            + "lvbiBnb19iYWNrKCkge3dpbmRvdy5oaXN0b3J5LmdvKC0xKTtyZXR1cm4gZmFsc2U7fWZ1bmN0aW9uIGN0dS"
            + "gpIHt2YXIgbGluayA9IGRvY3VtZW50ICYmIGRvY3VtZW50LnJlZmVycmVyO3ZhciBlc2NfbGluayA9ICIiO2"
            + "lmIChsaW5rKSB7dmFyIGUgPSAod2luZG93ICYmIHdpbmRvdy5lbmNvZGVVUklDb21wb25lbnQpID8gZW5jb2"
            + "RlVVJJQ29tcG9uZW50IDogZXNjYXBlO2VzY19saW5rID0gZShsaW5rKTt9bmV3IEltYWdlKCkuc3JjID0iL3"
            + "VybD9zYT1UJnVybD0iICsgZXNjX2xpbmsgKyAiJm9pPXVuYXV0aG9yaXplZHJlZGlyZWN0JmN0PW9yaWdpbm"
            + "xpbmsiO3JldHVybiBmYWxzZTt9PC9zY3JpcHQ+PC9oZWFkPjxib2R5IHRvcG1hcmdpbj0zIGJnY29sb3I9I2"
            + "ZmZmZmZiBtYXJnaW5oZWlnaHQ9Mz48ZGl2IGNsYXNzPWE+PGRpdiBjbGFzcz1iPjxmb250IHNpemU9KzE+PG"
            + "I+UmVkaXJlY3QgTm90aWNlPC9iPjwvZm9udD48L2Rpdj48L2Rpdj48ZGl2IGNsYXNzPWM+Jm5ic3A7VGhlIH"
            + "ByZXZpb3VzIHBhZ2UgaXMgc2VuZGluZyB5b3UgdG8gPGEgaHJlZj0iL3VybD9xPWh0dHA6Ly9rbm93eW91cm"
            + "1lbWUuY29tL21lbWVzL2Z1dHVyYW1hLWZyeS1ub3Qtc3VyZS1pZi14JmVpPTJ4ZG1VZFRiRHVmOXlnR2gwb0"
            + "RRQ0Emc2E9WCZvaT11bmF1dGhvcml6ZWRyZWRpcmVjdCZjdD10YXJnZXRsaW5rJnVzdD0xMzY1NjQ3MDc1Mj"
            + "QzNzUyJnVzZz1BRlFqQ05IZTZjR0FvNkdLVUJFLUNPY2dkTUc1SXJDR1V3Ij5odHRwOi8va25vd3lvdXJtZW"
            + "1lLmNvbS9tZW1lcy9mdXR1cmFtYS1mcnktbm90LXN1cmUtaWYteDwvYT4uPGJyPjxicj4mbmJzcDtJZiB5b3"
            + "UgZG8gbm90IHdhbnQgdG8gdmlzaXQgdGhhdCBwYWdlLCB5b3UgY2FuIDxhIGhyZWY9IiMiIG9uY2xpY2s9In"
            + "JldHVybiBnb19iYWNrKCk7IiBvbm1vdXNlZG93bj0iY3R1KCk7Ij5yZXR1cm4gdG8gdGhlIHByZXZpb3VzIH"
            + "BhZ2U8L2E+Ljxicj48YnI+PGJyPjwvZGl2PjwvYm9keT48L2h0bWw+";

    private static final int keylength = 192;

    private static final int iterations = 1000;

    final private SecretKey mKey;

    private Cipher mCipher;

    public AesEncryptionProvider(String salt) {
        this(SEKRIT, salt);
    }

    public AesEncryptionProvider(String passphrase, String salt) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_FACTORY);
            mKey = secretKeyFactory
                    .generateSecret(
                            new PBEKeySpec(passphrase.toCharArray(), salt.getBytes(), iterations,
                                    keylength));
            mCipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, PROVIDER);
            return;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error Creating AesEncryptionProvider");
    }

    @Override
    public String encrypt(String value) {
        final byte[] input = !TextUtils.isEmpty(value) ? value.getBytes() : new byte[0];
        return new String(Base64.encode(this.encrypt(input)));
    }

    @Override
    public byte[] encrypt(byte[] value) {
        try {
            final byte[] input = value != null ? value : new byte[0];
            mCipher.init(Cipher.ENCRYPT_MODE, mKey);
            return mCipher.doFinal(input);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Issues with encrypting");
    }

    @Override
    public String decrypt(String value) {

        if (value == null) {
            throw new IllegalArgumentException("Can not decrypt a null value");
        }

        final byte[] bytes = Base64.decode(value);
        return new String(this.decrypt(bytes));
    }

    @Override
    public byte[] decrypt(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("Can not decrypt a null value");
        }

        try {
            mCipher.init(Cipher.DECRYPT_MODE, mKey);
            return mCipher.doFinal(value);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Issues with encrypting");
    }
}
