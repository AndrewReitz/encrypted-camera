package com.andrewreitz.encryptedcamera.ui.activity;

import android.os.Bundle;
import android.widget.GridView;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.ui.adapter.GalleryAdapter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GalleryActivity extends BaseActivity {

    @Inject GalleryAdapter adapter;

    @InjectView(R.id.gallery) GridView gallery;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.inject(this);

        gallery.setAdapter(adapter);
    }
}
