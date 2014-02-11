package com.andrewreitz.encryptedcamera.encryption;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * @author Andrew
 */
public interface EncryptionProvider {
    public String encrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException;
    void encrypt(File in, File out) throws IOException, InvalidKeyException;
    public String decrypt(String value) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException;
    public byte[] encrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException;
    public byte[] decrypt(byte[] value) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException;
}
