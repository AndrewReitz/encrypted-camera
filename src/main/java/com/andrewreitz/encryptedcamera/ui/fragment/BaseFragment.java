package com.andrewreitz.encryptedcamera.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.andrewreitz.encryptedcamera.ui.activity.BaseActivity;

import butterknife.ButterKnife;
import icepick.Icepick;

public abstract class BaseFragment extends Fragment {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BaseActivity.get(this)
                .inject(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();

        // Release the views injected by butterknife
        ButterKnife.reset(this);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
