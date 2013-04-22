package co.nodeath.encryptedcamera.business.service;

import android.content.SharedPreferences;

/**
 * Service to make accessing shared preferences a little easier.
 *
 * @author eshapiro, areitz
 */
public class SharedPreferenceService {

    public static final String KEY_USE_PASSWORD = "use_password";

    public static final String KEY_ENCRYPT_PHOTOS = "encrypt";

    public static final String KEY_PASSWORD = "(V)(;,,;)(V)";

    //Place Preference Keys Here
    private final SharedPreferences mSharedPreferences;

    /**
     * Constructor
     *
     * @param sharedPreferences sharedpreferences to this service should use
     */
    public SharedPreferenceService(final SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
    }

    /**
     * Saves boolean user data in shared preferences.
     *
     * @param key   value to save and get on
     * @param value value to store
     */
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * gets boolean value from stored preferences
     *
     * @param key        key to look value up on
     * @param defaultVal value returned if no value is found
     * @return value stored with key if found, defaultVal otherwise
     */
    public boolean getBoolean(String key, boolean defaultVal) {
        return mSharedPreferences.getBoolean(key, defaultVal);
    }

    /**
     * Saves int user data in shared preferences.
     *
     * @param key   value to save and get on
     * @param value value to store
     */
    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * gets int value from stored preferences
     *
     * @param key        key to look value up on
     * @param defaultVal value returned if no value is found
     * @return value stored with key if found, defaultVal otherwise
     */
    public int getInt(String key, int defaultVal) {
        return mSharedPreferences.getInt(key, defaultVal);
    }

    /**
     * Saves float user data in shared preferences.
     *
     * @param key   value to save and get on
     * @param value value to store
     */
    public void saveFloat(String key, float value) {
        SharedPreferences.Editor prefs = mSharedPreferences.edit();
        prefs.putFloat(key, value);
        prefs.commit();
    }

    /**
     * gets float value from stored preferences
     *
     * @param key        key to look value up on
     * @param defaultVal value returned if no value is found
     * @return value stored with key if found, defaultVal otherwise
     */
    public float getFloat(String key, float defaultVal) {
        return mSharedPreferences.getFloat(key, defaultVal);
    }

    /**
     * Saves string user data in shared preferences.
     *
     * @param key   value to save and get on
     * @param value value to store
     */
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * gets string value from stored preferences
     *
     * @param key        key to look value up on
     * @param defaultVal value returned if no value is found
     * @return value stored with key if found, defaultVal otherwise
     */
    public String getString(String key, String defaultVal) {
        return mSharedPreferences.getString(key, defaultVal);
    }
}
