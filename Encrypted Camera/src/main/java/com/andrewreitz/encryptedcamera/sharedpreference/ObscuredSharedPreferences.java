package com.andrewreitz.encryptedcamera.sharedpreference;

import android.content.SharedPreferences;

import com.andrewreitz.encryptedcamera.encryption.EncryptionProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Warning, this gives a false sense of security.  If an attacker has enough access to acquire your
 * password store, then he almost certainly has enough access to acquire your source binary and
 * figure out your encryption key.  However, it will prevent casual investigators from acquiring
 * passwords, and thereby may prevent undesired negative publicity.
 */
public class ObscuredSharedPreferences implements SharedPreferences {

    private final EncryptionProvider mEncryptionProvider;
    private final SharedPreferences mSharedPreferences;

    public ObscuredSharedPreferences(SharedPreferences preferences,
            EncryptionProvider encryptionProvider) {
        this.mEncryptionProvider = checkNotNull(encryptionProvider);
        this.mSharedPreferences = checkNotNull(preferences);
    }

    public Editor edit() {
        return new Editor();
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException(); // left as an exercise to the reader
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        final String v = mSharedPreferences.getString(key, null);
        return v != null ? Boolean.parseBoolean(decrypt(v)) : defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        final String v = mSharedPreferences.getString(key, null);
        return v != null ? Float.parseFloat(decrypt(v)) : defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        final String v = mSharedPreferences.getString(key, null);
        return v != null ? Integer.parseInt(decrypt(v)) : defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        final String v = mSharedPreferences.getString(key, null);
        return v != null ? Long.parseLong(decrypt(v)) : defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        final String v = mSharedPreferences.getString(key, null);
        return v != null ? decrypt(v) : defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        Set<String> unencryptedValues = new HashSet<String>();
        Set<String> encyptedValues = mSharedPreferences.getStringSet(key, null);
        if (encyptedValues != null) {
            for (String value : mSharedPreferences.getStringSet(key, null)) {
                unencryptedValues.add(decrypt(value));
            }
            return unencryptedValues;
        } else {
            return defValues;
        }
    }

    @Override
    public boolean contains(String s) {
        return mSharedPreferences.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        mSharedPreferences
                .registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        mSharedPreferences
                .unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public String encrypt(String value) {
        return mEncryptionProvider.encrypt(value);
    }

    public String decrypt(String value) {
        return mEncryptionProvider.decrypt(value);
    }

    public class Editor implements SharedPreferences.Editor {

        protected SharedPreferences.Editor mEditor;

        public Editor() {
            this.mEditor = ObscuredSharedPreferences.this.mSharedPreferences.edit();
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            mEditor.putString(key, encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            mEditor.putString(key, encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            mEditor.putString(key, encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            mEditor.putString(key, encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            mEditor.putString(key, encrypt(value));
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            Set<String> encryptedValues = new HashSet<String>();
            for (String value : values) {
                encryptedValues.add(encrypt(value));
            }
            mEditor.putStringSet(key, encryptedValues);
            return this;
        }

        @Override
        public void apply() {
            mEditor.apply();
        }

        @Override
        public Editor clear() {
            mEditor.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return mEditor.commit();
        }

        @Override
        public Editor remove(String s) {
            mEditor.remove(s);
            return this;
        }
    }

}
