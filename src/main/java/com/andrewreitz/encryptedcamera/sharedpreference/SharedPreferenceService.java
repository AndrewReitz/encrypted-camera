package com.andrewreitz.encryptedcamera.sharedpreference;

/**
 * Interface for creating different sharedpreferenceservices
 */
public interface SharedPreferenceService {

    public void saveBoolean(String key, boolean value);

    public boolean getBoolean(String key, boolean defaultVal);

    public void saveInt(String key, int value);

    public int getInt(String key, int defaultVal);

    public void saveFloat(String key, float value);

    public float getFloat(String key, float defaultVal);

    public void saveString(String key, String value);

    public String getString(String key, String defaultVal);
}
