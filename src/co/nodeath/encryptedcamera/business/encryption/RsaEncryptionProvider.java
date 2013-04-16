package co.nodeath.encryptedcamera.business.encryption;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

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

    public RsaEncryptionProvider() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
            keyGen.initialize(KEY_SIZE);
            mKey = keyGen.generateKeyPair();

            mCipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String encrypt(String value) {
        return null;
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
        return null;
    }

    @Override
    public byte[] decrypt(byte[] value) {
        return new byte[0];
    }
}
