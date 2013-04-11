package co.nodeath.encryptedcamera.business.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Warning, this gives a false sense of security.  If an attacker has enough access to acquire your
 * password store, then he almost certainly has enough access to acquire your source binary and
 * figure out your encryption key.  However, it will prevent casual investigators from acquiring
 * passwords, and thereby may prevent undesired negative publicity.
 */
public class ObscuredSharedPreferences implements SharedPreferences {

    private static final String UTF8 = "utf-8";

    private static final String ENCRYPTION_TYPE = "PBEWithMD5AndDES";

    private static final String SEKRIT = "PGh0bWw+PGhlYWQ+PG1ldGEgaHR0cC1lcXVpdj0iY29udGVudC10eX"
            + "BlIiBjb250ZW50PSJ0ZXh0L2h0bWw7IGNoYXJzZXQ9VVRGLTgiPjx0aXRsZT5SZWRpcmVjdCBOb3RpY2U8L3"
            + "RpdGxlPjxzdHlsZT48IS0tCmJvZHksdGQsZGl2LC5wLGF7Zm9udC1mYW1pbHk6YXJpYWwsc2Fucy1zZXJpZn"
            + "0KZGl2LHRke2NvbG9yOiMwMDB9Ci5me2NvbG9yOiM2ZjZmNmZ9CmE6bGlua3tjb2xvcjojMDBjfQphOnZpc2"
            + "l0ZWR7Y29sb3I6IzU1MWE4Yn0KYTphY3RpdmV7Y29sb3I6cmVkfQpkaXYuYXtib3JkZXItdG9wOjFweCBzb2"
            + "xpZCAjYmJiO2JvcmRlci1ib3R0b206MXB4IHNvbGlkICNiYmI7YmFja2dyb3VuZDojZjJmMmYyO21hcmdpbi"
            + "10b3A6MWVtO3dpZHRoOjEwMCV9CmRpdi5ie3BhZGRpbmc6MC41ZW0gMDttYXJnaW4tbGVmdDoxMHB4fQpkaX"
            + "YuY3ttYXJnaW4tdG9wOjM1cHg7bWFyZ2luLWxlZnQ6MzVweH0KLS0+PC9zdHlsZT4KPHNjcmlwdD5mdW5jdG"
            + "lvbiBnb19iYWNrKCkge3dpbmRvdy5oaXN0b3J5LmdvKC0xKTtyZXR1cm4gZmFsc2U7fWZ1bmN0aW9uIGN0dS"
            + "gpIHt2YXIgbGluayA9IGRvY3VtZW50ICYmIGRvY3VtZW50LnJlZmVycmVyO3ZhciBlc2NfbGluayA9ICIiO2"
            + "lmIChsaW5rKSB7dmFyIGUgPSAod2luZG93ICYmIHdpbmRvdy5lbmNvZGVVUklDb21wb25lbnQpID8gZW5jb2"
            + "RlVVJJQ29tcG9uZW50IDogZXNjYXBlO2VzY19saW5rID0gZShsaW5rKTt9bmV3IEltYWdlKCkuc3JjID0iL3"
            + "VybD9zYT1UJnVybD0iICsgZXNjX2xpbmsgKyAiJm9pPXVuYXV0aG9yaXplZHJlZGlyZWN0JmN0PW9yaWdpbm"
            + "xpbmsiO3JldHVybiBmYWxzZTt9PC9zY3JpcHQ+PC9oZWFkPjxib2R5IHRvcG1hcmdpbj0zIGJnY29sb3I9I2"
            + "ZmZmZmZiBtYXJnaW5oZWlnaHQ9Mz48ZGl2IGNsYXNzPWE+PGRpdiBjbGFzcz1iPjxmb250IHNpemU9KzE+PG"
            + "I+UmVkaXJlY3QgTm90aWNlPC9iPjwvZm9udD48L2Rpdj48L2Rpdj48ZGl2IGNsYXNzPWM+Jm5ic3A7VGhlIH"
            + "ByZXZpb3VzIHBhZ2UgaXMgc2VuZGluZyB5b3UgdG8gPGEgaHJlZj0iL3VybD9xPWh0dHA6Ly9rbm93eW91cm"
            + "1lbWUuY29tL21lbWVzL2Z1dHVyYW1hLWZyeS1ub3Qtc3VyZS1pZi14JmVpPTJ4ZG1VZFRiRHVmOXlnR2gwb0"
            + "RRQ0Emc2E9WCZvaT11bmF1dGhvcml6ZWRyZWRpcmVjdCZjdD10YXJnZXRsaW5rJnVzdD0xMzY1NjQ3MDc1Mj"
            + "QzNzUyJnVzZz1BRlFqQ05IZTZjR0FvNkdLVUJFLUNPY2dkTUc1SXJDR1V3Ij5odHRwOi8va25vd3lvdXJtZW"
            + "1lLmNvbS9tZW1lcy9mdXR1cmFtYS1mcnktbm90LXN1cmUtaWYteDwvYT4uPGJyPjxicj4mbmJzcDtJZiB5b3"
            + "UgZG8gbm90IHdhbnQgdG8gdmlzaXQgdGhhdCBwYWdlLCB5b3UgY2FuIDxhIGhyZWY9IiMiIG9uY2xpY2s9In"
            + "JldHVybiBnb19iYWNrKCk7IiBvbm1vdXNlZG93bj0iY3R1KCk7Ij5yZXR1cm4gdG8gdGhlIHByZXZpb3VzIH"
            + "BhZ2U8L2E+Ljxicj48YnI+PGJyPjwvZGl2PjwvYm9keT48L2h0bWw+";

    private SharedPreferences mSharedPreferences;

    private Context mContext;

    public ObscuredSharedPreferences(Context context, SharedPreferences delegate) {
        this.mSharedPreferences = delegate;
        this.mContext = context;
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
        mSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    protected String encrypt(String value) {

        try {
            final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPTION_TYPE );
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance(ENCRYPTION_TYPE );
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Settings.Secure
                    .getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID)
                    .getBytes(UTF8), 20));
            return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected String decrypt(String value) {
        try {
            final byte[] bytes = value != null ? Base64.decode(value, Base64.DEFAULT) : new byte[0];
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPTION_TYPE);
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT.toCharArray()));
            Cipher pbeCipher = Cipher.getInstance(ENCRYPTION_TYPE);
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(Settings.Secure
                    .getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID)
                    .getBytes(UTF8), 20));
            return new String(pbeCipher.doFinal(bytes), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
