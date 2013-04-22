package co.nodeath.encryptedcamera.presentation.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import co.nodeath.encryptedcamera.R;
import co.nodeath.encryptedcamera.business.service.SharedPreferenceService;
import co.nodeath.encryptedcamera.presentation.dialog.SetPasswordDialog;

/**
 * @author Andrew
 */
public class MainActivity extends Activity implements SetPasswordDialog.SetPasswordDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void handleSetPassword(String password, String confirmPassword) {

    }

    @Override
    public void passwordCancelled() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(SharedPreferenceService.KEY_USE_PASSWORD, false);
    }
}