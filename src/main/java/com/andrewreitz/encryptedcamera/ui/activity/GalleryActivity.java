package com.andrewreitz.encryptedcamera.ui.activity;

import android.app.Application;
import android.os.Bundle;
import android.widget.GridView;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.ui.adapter.GalleryAdapter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class GalleryActivity extends BaseActivity {

    @Inject GalleryAdapter adapter;

    @InjectView(R.id.gallery) GridView gallery;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.inject(this);

        gallery.setAdapter(adapter);
    }

    @Override
    public void onTrimMemory(int level) {
        Timber.i("onTrimMemory() with level=%s", level);

        // Memory we can release here will help overall system performance, and
        // make us a smaller target as the system looks for memory

        if (level >= Application.TRIM_MEMORY_MODERATE) { // 60
            // Nearing middle of list of cached background apps; evict our
            // entire thumbnail cache
            Timber.i("evicting entire thumbnail cache");
            adapter.clearCache();

        } else if (level >= Application.TRIM_MEMORY_BACKGROUND) { // 40
            // Entering list of cached background apps; evict oldest half of our
            // thumbnail cache
            Timber.i("evicting oldest half of thumbnail cache");
            adapter.trimCache(adapter.getCacheSize() / 2);
        }
    }
}
