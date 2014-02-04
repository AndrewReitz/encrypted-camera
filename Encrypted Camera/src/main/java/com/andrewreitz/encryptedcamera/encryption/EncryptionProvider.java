package com.andrewreitz.encryptedcamera.encryption;

/**
 * @author Andrew
 */
public interface EncryptionProvider {
    public String encrypt(String value);
    public String decrypt(String value);
    public byte[] encrypt(byte[] value);
    public byte[] decrypt(byte[] value);
}
