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

package com.andrewreitz.encryptedcamera.ui.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.cache.ThumbnailCache;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import timber.log.Timber;

public class GalleryAdapter extends BindableAdapter<File> {
    private List<File> images = Collections.emptyList();
    private final int viewSize;
    private final LruCache<String, Bitmap> cache;

    public GalleryAdapter(@NotNull Context context, @NotNull List<File> images, @NotNull LruCache<String, Bitmap> cache) {
        super(context);
        this.viewSize = context.getResources().getDimensionPixelSize(R.dimen.gridview_image);
        this.images = images;
        this.cache = cache;
    }

    @Override public int getCount() {
        return images.size();
    }

    @Override public File getItem(int position) {
        return images.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.gallery_item, null);
    }

    @Override public void bindView(File file, int position, View view) {
        // TODO Make this faster! (RenderScript?)
        ImageView imageView = ButterKnife.findById(view, R.id.gallery_imageview);
        // Cancel any pending thumbnail task, since this view is now bound
        // to new thumbnail
        final ThumbnailAsyncTask oldTask = (ThumbnailAsyncTask) view.getTag();
        if (oldTask != null) {
            oldTask.cancel(false);
        }

        // Cache enabled, try looking for cache hit
        final Bitmap cachedResult = cache.get(file.getName());
        if (cachedResult != null) {
            imageView.setImageBitmap(cachedResult);
            return;
        }

        // If we arrived here, either cache is disabled or cache miss, so we
        // need to kick task to load manually
        final ThumbnailAsyncTask task = new ThumbnailAsyncTask(imageView);
        imageView.setImageBitmap(null);
        imageView.setTag(task);
        task.execute(file);
    }

    private class ThumbnailAsyncTask extends AsyncTask<File, Void, Bitmap> {
        private final ImageView target;

        public ThumbnailAsyncTask(ImageView target) {
            this.target = target;
        }

        @Override
        protected void onPreExecute() {
            target.setTag(this);
        }

        @Override
        protected Bitmap doInBackground(File... params) {
            Bitmap result = null;
            File file = params[0];
            try {
                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, viewSize, viewSize, false);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageData = baos.toByteArray();
                result = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            } catch (Exception e) {
                Timber.e(e, "Error resizing image");
            }

            cache.put(file.getName(), result);
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (target.getTag() == this) {
                target.setImageBitmap(result);
                target.setTag(null);
            }
        }
    }
}
