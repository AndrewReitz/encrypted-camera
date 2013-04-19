package co.nodeath.encryptedcamera.business.encryption;

import org.bouncycastle.util.encoders.Base64;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author Andrew
 */
public class RsaEncryptionProvider implements IEncryptionProvider {

    private static final String PROVIDER = "BC";

    private static final String ALGORITHM = "RSA";

    private static final String ALGORITHM_MODE_PADDING = "RSA/ECB/PKCS1Padding";

    private static final int KEY_SIZE = 1024;

    private Cipher mCipher;

    private KeyPair mKey;

    public RsaEncryptionProvider(KeyPair key) {
        mKey = key;
        try {
            mCipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String encrypt(String value) {
        final byte[] input = value != null ? value.getBytes() : new byte[0];
        return new String(Base64.encode(this.encrypt(input)));
    }

    @Override
    public byte[] encrypt(byte[] value) {
        try {
            final byte[] input = value != null ? value : new byte[0];
            mCipher.init(Cipher.ENCRYPT_MODE, mKey.getPublic());
            return mCipher.doFinal(input);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error Encrypting");
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
        try {
            mCipher.init(Cipher.DECRYPT_MODE, mKey.getPrivate());
            return mCipher.doFinal(value);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error Decrypting");
    }

    public static KeyPair generateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
            keyGen.initialize(KEY_SIZE);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error Generating Key");
    }
}
