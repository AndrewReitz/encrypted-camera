package com.andrewreitz.encryptedcamera.encryption;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

/**
 * @author Andrew
 */
public interface EncryptionProvider {
    void encrypt(File in, File out) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException;
    void decrypt(File in, File out) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException;
    String encrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException;
    String decrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidAlgorithmParameterException;
    byte[] encrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException;
    byte[] decrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException;
    void setSecretKey(SecretKey secretKey);
}
