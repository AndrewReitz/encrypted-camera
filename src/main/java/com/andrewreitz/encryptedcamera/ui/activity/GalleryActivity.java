/*
 * Copyright (C) 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andrewreitz.encryptedcamera.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.ui.adapter.GalleryAdapter;
import com.google.common.net.MediaType;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GalleryActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Inject GalleryAdapter adapter;

    @InjectView(R.id.gallery) GridView gallery;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.inject(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        gallery.setAdapter(adapter);
        gallery.setOnItemClickListener(this);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Open the files to other apps.  If other apps start bogarting thumbnails and not cleaning
        // up properly might need to add full screen images
        File file = adapter.getItem(position);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(file.getAbsolutePath()), MediaType.ANY_IMAGE_TYPE.toString());
        startActivity(intent);
    }
}
