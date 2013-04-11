package co.nodeath.encryptedcamera.business.encryption;

/**
 * @author Andrew
 */
public interface IEncryptionProvider {
    public String encrypt(String value);
    public String decrypt(String value);
    public byte[] encrypt(byte[] value);
    public byte[] decrypt(byte[] value);
}
