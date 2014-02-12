package com.andrewreitz.encryptedcamera.encryption;

import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Andrew
 */
public class EncryptionProviderImp implements EncryptionProvider {

    private SecretKey secretKey;
    private Cipher cipher;

    public EncryptionProviderImp(Cipher cipher, SecretKey secretKey) {
        this.cipher = checkNotNull(cipher);
        this.secretKey = checkNotNull(secretKey);
    }

    @Override
    public String encrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        final byte[] input = !TextUtils.isEmpty(value) ? value.getBytes() : new byte[0];
        return Base64.encodeToString(
                this.encrypt(input),
                Base64.DEFAULT
        );
    }

    @Override
    public byte[] encrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final byte[] input = value != null ? value : new byte[0];
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(input);
    }

    /**
     * Encrypts the in file and places it to the out file
     *
     * @param in  input file
     * @param out output file
     * @throws IOException
     * @throws InvalidKeyException
     */
    @Override
    public void encrypt(File in, File out) throws IOException, InvalidKeyException {
        checkNotNull(in);
        checkNotNull(out);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), cipher);

        copy(is, os);

        os.close();
    }

    //TODO Place Decrypt For Streams / Files

    @Override
    public String decrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        checkNotNull(value);

        final byte[] bytes = Base64.decode(value, Base64.DEFAULT);
        return new String(this.decrypt(bytes));
    }

    @Override
    public byte[] decrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        checkNotNull(value);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(value);
    }

    /**
     * Copies the input stream to the output stream
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }
}
