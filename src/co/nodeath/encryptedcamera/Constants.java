package co.nodeath.encryptedcamera;

/**
 * Constants File The one stop place for changing values in an application. Hard and fast rules, any
 * value that may change between dev/prod builds should go here so there is only one file to look at
 * and go through
 *
 * @author areitz
 */
public final class Constants {

    public static final String EXTERNAL_FILE_APPICATION_FOLDER = "EncryptedCamera";
    public static final String EXTERNAL_FILE_HIDEEN_FOLDER = ".hidden";
    public static final String EXTERNAL_FILE_ENCRYPTED_FOLDER = "Encrypted";

    // Suppress default constructor for noninstantiability
    private Constants() {
        throw new AssertionError();
    }
}
