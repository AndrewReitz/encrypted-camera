package com.andrewreitz.encryptedcamera.encryption;

import android.text.TextUtils;

import org.bouncycastle.util.encoders.Base64;

import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrew
 */
public abstract class AbstractEncryptionProvider implements EncryptionProvider {

    private SecretKey secretKey;
    private Cipher cipher;

    public AbstractEncryptionProvider(Cipher cipher, SecretKey secretKey) {
        this.cipher = checkNotNull(cipher);
        this.secretKey = checkNotNull(secretKey);
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
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(input);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            // TODO
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
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(value);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            // TODO
            e.printStackTrace();
        }

        throw new RuntimeException("Issues with encrypting");
    }
}
