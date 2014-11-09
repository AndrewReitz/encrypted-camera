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

package com.andrewreitz.encryptedcamera.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.sharedpreference.AppPreferenceManager;
import com.andrewreitz.encryptedcamera.ui.adapter.GalleryAdapter;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.google.common.net.MediaType.ANY_IMAGE_TYPE;

public class GalleryFragment extends BaseFragment implements AdapterView.OnItemClickListener {

  @Inject AppPreferenceManager preferenceManager;
  @Inject GalleryAdapter adapter;

  @InjectView(R.id.gallery) GridView gallery;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_gallery, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override public void onStart() {
    super.onStart();
    gallery.setAdapter(adapter);
    gallery.setOnItemClickListener(this);
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    if (!preferenceManager.hasSeenExternalLaunchWarning()) {
      @SuppressWarnings("ConstantConditions") AlertDialog.Builder builder =
          new AlertDialog.Builder(getActivity());
      builder.setTitle(getString(R.string.warning))
          .setMessage(
              String.format(getString(R.string.leaving_app_message), getString(R.string.app_name)))
          .setPositiveButton(android.R.string.ok, null)
          .create()
          .show();

      preferenceManager.setHasSeenExternalLaunchWarning(true);
      return;
    }

    // Open the files to other apps.  If other apps start bogarting thumbnails and not cleaning
    // up properly might need to add full screen images
    File file = adapter.getItem(position);
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri uri = Uri.fromFile(file);
    intent.setDataAndType(uri, ANY_IMAGE_TYPE.toString());
    startActivity(intent);
  }
}
